package com.jtexplorer.job;

import com.jtexplorer.entity.JkExceptionInfo;
import com.jtexplorer.entity.JkResultInfo;
import com.jtexplorer.entity.SystemLog;
import com.jtexplorer.redis.MyLogRedisCache;
import com.jtexplorer.service.IJkExceptionInfoService;
import com.jtexplorer.service.IJkResultInfoService;
import com.jtexplorer.service.ISystemLogService;
import com.jtexplorer.utils.ListUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Configuration
@EnableScheduling
public class SystemJob {
    //项目名
    @Value("${jtConfig.jtLog.projectName}")
    private String projectName;

    @Resource
    private MyLogRedisCache myLogRedisCache;
    @Resource
    private ISystemLogService systemLogService;
    @Resource
    private IJkResultInfoService jkResultInfoService;
    @Resource
    private IJkExceptionInfoService jkExceptionInfoService;

    /**
     * systemLog持久化定时任务
     */
    @Scheduled(cron = "${jtConfig.jtLog.job.systemLogJob}")
    private void systemLogJob() {
        Set<String> stringSet = myLogRedisCache.getKeys("systemLog_" + projectName + "_*");
        List<SystemLog> systemLogList = new ArrayList<>();
        for (String key : stringSet) {
            SystemLog systemLog = myLogRedisCache.getCacheObject(key, SystemLog.class);
            systemLogList.add(systemLog);
            myLogRedisCache.deleteObject(key);
        }
        if (ListUtil.isNotEmpty(systemLogList)) {
            systemLogService.saveBatch(systemLogList);
        }
    }

    /**
     * jkResultInfo持久化定时任务
     */
    @Scheduled(cron = "${jtConfig.jtLog.job.jkResultInfoJob}")
    private void jkResultInfoJob() {
        Set<String> stringSet = myLogRedisCache.getKeys("jkResultInfo_" + projectName + "_*");
        List<JkResultInfo> jkResultInfoList = new ArrayList<>();
        for (String key : stringSet) {
            JkResultInfo jkResultInfo = myLogRedisCache.getCacheObject(key, JkResultInfo.class);
            jkResultInfoList.add(jkResultInfo);
            myLogRedisCache.deleteObject(key);
        }
        if (ListUtil.isNotEmpty(jkResultInfoList)) {
            jkResultInfoService.saveBatch(jkResultInfoList);
        }
    }

    /**
     * jkResultInfo持久化定时任务
     */
    @Scheduled(cron = "${jtConfig.jtLog.job.jkExceptionInfoJob}")
    private void jkExceptionInfoJob() {
        Set<String> stringSet = myLogRedisCache.getKeys("jkExceptionInfo_" + projectName + "_*");
        List<JkExceptionInfo> jkExceptionInfoList = new ArrayList<>();
        for (String key : stringSet) {
            JkExceptionInfo jkExceptionInfo = myLogRedisCache.getCacheObject(key, JkExceptionInfo.class);
            jkExceptionInfoList.add(jkExceptionInfo);
            myLogRedisCache.deleteObject(key);
        }
        if (ListUtil.isNotEmpty(jkExceptionInfoList)) {
            jkExceptionInfoService.saveBatch(jkExceptionInfoList);
        }
    }

}
