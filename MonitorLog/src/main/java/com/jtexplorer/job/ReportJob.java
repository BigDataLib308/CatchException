package com.jtexplorer.job;

import com.jtexplorer.entity.dto.LogCount;
import com.jtexplorer.entity.query.JkExceptionInfoQuery;
import com.jtexplorer.service.ISystemLogService;
import com.jtexplorer.utils.ListUtil;
import com.jtexplorer.utils.LogTimeTools;
import com.jtexplorer.utils.MyFileWriter;
import com.jtexplorer.utils.WxCpSendMsgUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Configuration
@EnableScheduling
public class ReportJob {

    @Value("${jtConfig.jtLog.projectName}")
    private String projectName;
    @Resource
    private ISystemLogService service;
    @Resource
    private WxCpSendMsgUtil wxCpSendMsgUtil;
    @Value("${jtConfig.jtLog.file.url}")
    private String url;


    /**
     * 自动生成报告单  --在次日的凌晨2点进行
     */
    @Scheduled(cron = "${jtConfig.jtLog.job.reportJob}")
    private void reportJob() throws Exception {

        //时间
        Date endDate = LogTimeTools.getDateByMoreDay(new Date(), -1);
        Date startDate = LogTimeTools.getDateByMoreDay(new Date(), -8);
        String endDateStr = LogTimeTools.transformDateFormat(endDate, "yyyy-MM-dd");
        String startDateStr = LogTimeTools.transformDateFormat(startDate, "yyyy-MM-dd");

        Date startTime = LogTimeTools.transformDateFormatStringToDate(startDateStr + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
        Date endTime = LogTimeTools.transformDateFormatStringToDate(endDateStr + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
        String startTimeStr = LogTimeTools.transformDateFormat(startTime, "yyyy-MM-dd HH:mm");
        String endTimeStr = LogTimeTools.transformDateFormat(endTime, "yyyy-MM-dd HH:mm");

        // 生成报告
        JkExceptionInfoQuery query = new JkExceptionInfoQuery();
        query.buildSavePath();
        query.buildTxtPath();

        LogCount logCountAll = new LogCount();
        logCountAll.setCount(0);
        logCountAll.setFailCount(0);
        logCountAll.setErrorCount(0);
        StringBuilder urlReport = new StringBuilder();

        List<LogCount> logCounts = service.selectLogCount(startTimeStr, endTimeStr);
        if (ListUtil.isNotEmpty(logCounts)) {
            for (LogCount v : logCounts) {
                logCountAll.add(v);
                urlReport.append("=》").append(v.getUrl()).append("\n");
                //请求总数--------------
                urlReport.append("  ").append("请求总数：").append(v.getCount());
                // 请求成功次数 -------------------
                urlReport.append(" | ").append("成功次数：").append(v.getSuccess());
                //请求失败次数------------------
                urlReport.append(" | ").append("失败次数：").append(v.getUnSuccess()).append("\n");
            }
        }

        StringBuilder noticeContent = new StringBuilder();
        StringBuilder fileContent = new StringBuilder();
        noticeContent.append("【").append(projectName).append("项目使用情况报告】").append("\n");
        noticeContent.append("汇总起止时间：").append(LogTimeTools.transformDateFormat(startTime, "yyyy-MM-dd HH:mm")).append(" ~ ").append(LogTimeTools.transformDateFormat(endTime, "yyyy-MM-dd HH:mm")).append("\n");
        noticeContent.append("接口请求总数：").append(logCountAll.getCount()).append("\n");
        noticeContent.append("请求成功次数：").append(logCountAll.getSuccess()).append("\n");
        noticeContent.append("请求失败次数：").append(logCountAll.getUnSuccess()).append("\n");


        fileContent.append(noticeContent.toString());
        String downloadPath = query.getTxtReturnPath();
        noticeContent.append("详情下载地址：").append(url).append(downloadPath);
        fileContent.append(urlReport);

        //保存地址
        MyFileWriter.writeString(query.getTxtSavePath(), fileContent.toString());
        // 发送企业微信
        wxCpSendMsgUtil.sendTextMsg(noticeContent.toString());
    }
}
