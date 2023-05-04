package com.jtexplorer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 异常信息
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class JkExceptionInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "jk_exce_id", type = IdType.AUTO)
    private Long jkExceId;


    private String jkExceClassName;


    private String jkExceMessage;


    private String jkExceTrace;


    private Integer jkExceStatus;


    private String jkExceRemark;


    private Date jkExceDbCreateTime;


    private Date jkExceDbUpdateTime;


    private Integer jkExceIsDelete;


    private String jkExecCause;


    private String jkExecMethodRemark;


    private String jkExecParamsJson;

}
