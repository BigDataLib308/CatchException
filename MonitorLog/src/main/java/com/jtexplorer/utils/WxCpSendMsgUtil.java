package com.jtexplorer.utils;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 发送企业微信通知
 */
@Component
@Slf4j
public class WxCpSendMsgUtil {

    private static Integer agentId ;

    @Resource
    private WxCpService wxCpService;
    //告警信息推送
    @Value("${jtConfig.jtLog.noticeEnable}")
    private String noticeEnable;

    public void sendTextMsg(String content) throws Exception {
        if ("true".equals(noticeEnable)) {
            WxCpMessage cpMessage = WxCpMessage
                    .TEXT()
                    .agentId(agentId)
                    .toUser("@all")
                    .content(content)
                    .build();
            wxCpService.getMessageService().send(cpMessage);
        }
    }


}
