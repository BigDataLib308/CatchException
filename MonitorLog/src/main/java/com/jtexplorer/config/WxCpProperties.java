package com.jtexplorer.config;

import lombok.Data;

@Data
public class WxCpProperties {
    /**
     * 设置企业微信的corpId
     */
    private String corpId;

    /**
     * 设置企业微信应用的AgentId
     */
    private Integer agentId;

    /**
     * 设置企业微信应用的Secret
     */
    private String secret;

    /**
     * 设置企业微信应用的token
     */
    private String token;

    /**
     * 设置企业微信应用的EncodingAESKey
     */
    private String aesKey;

}
