package com.jtexplorer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 接口返回数据异常
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class JkResultInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "jk_resu_id", type = IdType.AUTO)
    private Integer jkResuId;


    private Integer jkResuSuccess;


    private String jkResuFailReason;


    private String jkResuFailReasonShow;


    private String jkResuClassName;


    private Long jkResuCostTime;


    private Integer jkResuIsOvertime;


    private Integer jkResuStatus;


    private String jkResuRemark;


    private Date jkResuDbCreateTime;


    private Date jkResuDbUpdateTime;


    private Integer jkResuIsDelete;


    private String jkResuUrl;


}
