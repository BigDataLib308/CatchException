package com.jtexplorer.config;

import lombok.Data;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@Component
public class WxCpConfiguration {

    private static String cropId = "";
    private static Integer agentId ;
    private static String secret = "";

    private WxCpProperties properties;
    private WxCpService wxCpService;

    @Bean
    public WxCpService wxCpService() {
        final List configs = new ArrayList<>();
        configs.add(new HashMap<>());
        if (configs == null) {
            throw new RuntimeException("相关配置错误！");
        }

        WxCpService service = new WxCpServiceImpl();
        WxCpDefaultConfigImpl configStorage = new WxCpDefaultConfigImpl();
        configStorage.setAgentId(agentId);
        configStorage.setCorpId(cropId);
        configStorage.setCorpSecret(secret);
        service.setWxCpConfigStorage(configStorage);
        wxCpService =service;
        return service;

    }

    @Autowired
    public WxCpConfiguration() {
        WxCpProperties properties = new WxCpProperties();
        properties.setCorpId(this.cropId);
        properties.setAgentId(this.agentId);
        properties.setSecret(this.secret);
        this.properties = properties;
    }

}
