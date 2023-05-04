package com.jtexplorer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SystemLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "sylo_id",type = IdType.AUTO)
    private Long syloId;


    private String syloLoginUserId;


    private String syloIpAddress;


    private String syloRequestData;


    private String syloResponseData;


    private Date syloCreateTime;


    private String syloDescription;


    private String syloRequestUrl;


    private Long syloRequestTime;


    private String syloMethodName;


    private String syloClient;


    private String syloAccount;


    private String syloUserRealName;


    private String syloClassName;


}
