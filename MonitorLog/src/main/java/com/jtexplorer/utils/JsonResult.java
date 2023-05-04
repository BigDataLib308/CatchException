package com.jtexplorer.utils;


import com.jtexplorer.entity.enums.RequestEnum;
import jodd.util.StringUtil;
import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@ToString
public class JsonResult {
    /**
     * 请求是否成功
     */
    private boolean success;
    /**
     * 返回的记录总数
     */
    private long totalSize;
    /**
     * 错误码
     */
    private String tip;
    /**
     * 维护人员看到的错误原因
     */
    private String failReason;
    /**
     * 客户显示的错误原因
     */
    private String failReasonShow;
    private List root;
    private Object object;
    private String requestId;
    private String serviceId;
    private String item;




    public String getFailReasonShow() {
        if (StringUtil.isNotEmpty(failReasonShow)) {
            return failReasonShow;
        } else {
            return failReason;
        }
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
        this.failReasonShow = failReason;
    }

    public void buildNew(boolean success, long totalSize, String tip, String failReason, List root, Object object,String requestId,String serviceId,String item) {
        this.tip = tip;
        this.failReason = failReason;
        this.root = root;
        this.object = object;
        this.success = success;
        this.totalSize = totalSize;
        this.requestId = requestId;
        this.serviceId = serviceId;
        this.item = item;
    }

    public void buildFalseNew(String tip, String failReason) {
        this.tip = tip;
        this.failReason = failReason;
        this.success = false;
    }

    public void buildTrueNew(long totalSize, List root) {
        this.root = root;
        this.success = true;
        this.totalSize = totalSize;
    }

    public void buildTrueNew(Object object) {
        this.object = object;
        this.success = true;
    }

    public void buildTrueNew() {
        this.success = true;
    }

    public void buildFalseNew(RequestEnum requestEnum) {
        this.tip = requestEnum.getCode();
        this.failReason = requestEnum.getMeaning();
        this.failReasonShow = requestEnum.getMeaning();
        this.success = false;
    }

    public void buildFalseNew(RequestEnum requestEnum, String reason) {
        this.tip = requestEnum.getCode();
        this.failReason = requestEnum.getMeaning() + reason;
        this.failReasonShow = reason;
        this.success = false;
    }

    public JsonResult buildFalseNewReturn(RequestEnum requestEnum, String reason) {
        this.tip = requestEnum.getCode();
        this.failReason = requestEnum.getMeaning() + reason;
        this.failReasonShow = reason;
        this.success = false;
        return this;
    }

    public AjaxResult buildFalseNewReturnAjaxResult(RequestEnum requestEnum, String reason) {
        this.tip = requestEnum.getCode();
        this.failReason = requestEnum.getMeaning() + reason;
        this.failReasonShow = reason;
        this.success = false;
        return new AjaxResult().addError(this.getFailReason()).addError(this.getFailReasonShow());
    }

    public AjaxResult buildReturnAjaxResult() {
        return new AjaxResult().addError(this.getFailReason()).addError(this.getFailReasonShow());
    }
}
