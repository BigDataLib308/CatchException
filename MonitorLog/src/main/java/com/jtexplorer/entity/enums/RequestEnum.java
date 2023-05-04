package com.jtexplorer.entity.enums;

/**
 * RequestEnum class
 *
 */
public enum RequestEnum {

    /**
     * 用于接口返回错误标识
     */

    ERROR_REGISTER_REGISTERED("已存在注册信息，不可重复注册", "10001"),
    ERROR_REGISTER_NO_USER("不存在要注册的用户信息", "10002"),
    ERROR_REGISTER_MORE_USER("存在多个要注册的用户信息", "10003"),
    ERROR_REGISTER_FAIL("注册失败，请重试", "10004"),

    ERROR_LOGIN_NOT_LOGIN("用户未登录或登录信息已失效","20001"),
    ERROR_LOGIN_INFO("登录信息有误","20002"),
    ERROR_LOGIN_NOT_SETTING_ROLE("选择登录角色失败，登录人无该角色","20003"),
    ERROR_LOGIN_NOT_SETTING_ORG("选择登录角色失败，登录人无该机构","20004"),
    ERROR_LOGIN_NO_ROLE_INFO("登录人未分配角色","20005"),
    ERROR_LOGIN_ACCOUNT_EMPTY("登录账号不可为空","20006"),
    ERROR_LOGIN_PASSWORD_EMPTY("登录密码不可为空","20007"),
    ERROR_LOGIN_NOT_REGISTER("要登录的用户信息还没有注册","20008"),


    ERROR_DATABASE_INSERT_ERROR("插入数据库时数据格式错误","30001"),
    ERROR_DATABASE_UPDATE_ERROR("数据库更新操作时数据格式错误","30002"),
    ERROR_DATABASE_DELETE_ERROR("执行数据库删除操作时发生的错误","30003"),
    ERROR_DATABASE_PARAM_ERROR("参数未传值或参数格式错误","30004"),
    ERROR_DATABASE_SELECT_ERROR("数据库查询错误","30005"),
    ERROR_DATABASE_QUERY_NO_DATA("无数据","30006"),
    ERROR_DATABASE_UPDATE_NO_KEY("更新时缺失主键条件","30007"),
    ERROR_DATABASE_DELETE_NO_KEY("删除时缺失主键条件","30008"),
    ERROR_DATABASE_INSERT_HAVE_NULL("新增时有必填项为空","30009"),
    ERROR_DATABASE_QUERY_ERROR("查询参数错误","30010"),


    ERROR_EXCEPTION_DATE("程序异常。时间格式化错误。","40001"),
    ERROR_SYSTEM_TIME_ERROR("系统错误-时间错误","40002"),
    ERROR_SYSTEM_ERROR("系统错误","40003"),
    ERROR_SYSTEM_CONFIG_ERROR("系统设置错误","40004"),
    ERROR_CLIENT_TIME_OUT("响应超时","40005"),
    ERROR_SEND_TIME_OUT("发送失败超时","40006"),
    ERROR_THROW("抛出异常","40007"),

    ERROR_VERIFY_ERROR("验证失败","50001"),
    ERROR_PAY("支付失败","50002"),
    ERROR_REFUND("退款申请失败","50003"),
    ERROR_VOTED("不能重复投票","50004"),
    ERROR_SEND("发送失败","50005"),
    ERROR_VOTE_NO_SELECT("无法查看该记录","50006"),

    ERROR_IMPORT_EXCEL("excel导入错误，excel格式不对","60001"),
    ERROR_EXCEL_IMPORT("excel导入失败","60002"),
    ERROR_EXCEL_EXPORT("excel导出失败","60003"),
    ERROR_VIRTUAL_KEY_ADD("虚拟按键中间信息插入失败，请重试","60004"),
    ERROR_VIRTUAL_KEY("虚拟按键操作失败，请重试","60005"),
    ERROR_LOGIN_NO_PERMISSION("无权限","60006"),
    REQUEST_ERROR_DATABASE_QUERY_NO_DATA("无数据。", "60007"),

    ;

    /**
     * 说明
     */
    private String meaning;

    /**
     * 代码
     */
    private String code;

    public String getMeaning() {
        return meaning;
    }

    public String getCode() {
        return code;
    }

    RequestEnum(String meaning, String code) {
        this.meaning = meaning;
        this.code = code;
    }
}
