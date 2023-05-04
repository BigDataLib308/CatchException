package com.jtexplorer.entity.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jtexplorer.entity.JkExceptionInfo;
import com.jtexplorer.entity.dto.JkExceptionInfoDTO;
import com.jtexplorer.utils.StringUtil;
import lombok.Data;

/**
 * 异常信息 查询类
 *
 * @author lqq
 * @since 2022-09-22
 */
@Data
public class JkExceptionInfoQuery extends QueryParamLog {
    /**
     * 原类
     */
    private JkExceptionInfoDTO param;
    private QueryWrapper<JkExceptionInfo> query;
    private UpdateWrapper<JkExceptionInfo> update;
    private String isHaveBlob;

    // likeParam
    // 异常所在的类
    private String jkExceClassNameIsLike;
    // 异常信息
    private String jkExceMessageIsLike;
    // 备注
    private String jkExceRemarkIsLike;
    // 异常产生的原因
    private String jkExecCauseIsLike;
    //方法说明
    private String jkExecMethodRemarkIsLike;
    //方法参数
    private String jkExecParamsJsonIsLike;

    public QueryWrapper<JkExceptionInfo> buildExample() {
        query = new QueryWrapper<>();
        update = new UpdateWrapper<>();
        if (getParam() == null) {
            setParam(new JkExceptionInfoDTO());
        }


        //
        if (StringUtil.isNotEmpty(getParam().getJkExceId())) {
            query.eq("jk_exce_id", getParam().getJkExceId());
            update.eq("jk_exce_id", getParam().getJkExceId());
        }

        // 异常所在的类
        if (StringUtil.isNotEmpty(getParam().getJkExceClassName())) {
            query.eq("jk_exce_class_name", getParam().getJkExceClassName());
            update.eq("jk_exce_class_name", getParam().getJkExceClassName());
        }
        if (StringUtil.isNotEmpty(getJkExceClassNameIsLike())) {
            query.like("jk_exce_class_name", getJkExceClassNameIsLike());
            update.like("jk_exce_class_name", getJkExceClassNameIsLike());
        }

        // 异常信息
        if (StringUtil.isNotEmpty(getParam().getJkExceMessage())) {
            query.eq("jk_exce_message", getParam().getJkExceMessage());
            update.eq("jk_exce_message", getParam().getJkExceMessage());
        }
        if (StringUtil.isNotEmpty(getJkExceMessageIsLike())) {
            query.like("jk_exce_message", getJkExceMessageIsLike());
            update.like("jk_exce_message", getJkExceMessageIsLike());
        }

        // 异常链路
        if (StringUtil.isNotEmpty(getParam().getJkExceTrace())) {
            query.eq("jk_exce_trace", getParam().getJkExceTrace());
            update.eq("jk_exce_trace", getParam().getJkExceTrace());
        }

        // 状态
        if (StringUtil.isNotEmpty(getParam().getJkExceStatus())) {
            query.eq("jk_exce_status", getParam().getJkExceStatus());
            update.eq("jk_exce_status", getParam().getJkExceStatus());
        }

        // 备注
        if (StringUtil.isNotEmpty(getParam().getJkExceRemark())) {
            query.eq("jk_exce_remark", getParam().getJkExceRemark());
            update.eq("jk_exce_remark", getParam().getJkExceRemark());
        }
        if (StringUtil.isNotEmpty(getJkExceRemarkIsLike())) {
            query.like("jk_exce_remark", getJkExceRemarkIsLike());
            update.like("jk_exce_remark", getJkExceRemarkIsLike());
        }

        // 数据创建时间
        if (StringUtil.isNotEmpty(getParam().getJkExceDbCreateTime())) {
            query.eq("jk_exce_db_create_time", getParam().getJkExceDbCreateTime());
            update.eq("jk_exce_db_create_time", getParam().getJkExceDbCreateTime());
        }

        // 数更新时间
        if (StringUtil.isNotEmpty(getParam().getJkExceDbUpdateTime())) {
            query.eq("jk_exce_db_update_time", getParam().getJkExceDbUpdateTime());
            update.eq("jk_exce_db_update_time", getParam().getJkExceDbUpdateTime());
        }

        // 是否删除  1是
        if (StringUtil.isNotEmpty(getParam().getJkExceIsDelete())) {
            query.eq("jk_exce_is_delete", getParam().getJkExceIsDelete());
            update.eq("jk_exce_is_delete", getParam().getJkExceIsDelete());
        }

        // 异常产生的原因
        if (StringUtil.isNotEmpty(getParam().getJkExecCause())) {
            query.eq("jk_exec_cause", getParam().getJkExecCause());
            update.eq("jk_exec_cause", getParam().getJkExecCause());
        }
        if (StringUtil.isNotEmpty(getJkExecCauseIsLike())) {
            query.like("jk_exec_cause", getJkExecCauseIsLike());
            update.like("jk_exec_cause", getJkExecCauseIsLike());
        }

        //方法说明
        if (StringUtil.isNotEmpty(getParam().getJkExecMethodRemark())) {
            query.eq("jk_exec_method_remark", getParam().getJkExecMethodRemark());
            update.eq("jk_exec_method_remark", getParam().getJkExecMethodRemark());
        }
        if (StringUtil.isNotEmpty(getJkExecMethodRemarkIsLike())) {
            query.like("jk_exec_method_remark", getJkExecMethodRemarkIsLike());
            update.like("jk_exec_method_remark", getJkExecMethodRemarkIsLike());
        }
        //方法参数
        if (StringUtil.isNotEmpty(getParam().getJkExecParamsJson())) {
            query.eq("jk_exec_params_json", getParam().getJkExecParamsJson());
            update.eq("jk_exec_params_json", getParam().getJkExecParamsJson());
        }
        if (StringUtil.isNotEmpty(getJkExecParamsJsonIsLike())) {
            query.like("jk_exec_params_json", getJkExecParamsJsonIsLike());
            update.like("jk_exec_params_json", getJkExecParamsJsonIsLike());
        }

// 排序
        if (StringUtil.isNotEmpty(getOrderItem())) {
            if (StringUtil.isEmpty(getOrderType())) {
                query.orderByAsc(getOrderItem().split(","));
            } else if ("asc".equals((getOrderType()))) {
                query.orderByAsc(getOrderItem().split(","));
            } else if ("desc".equals((getOrderType()))) {
                query.orderByDesc(getOrderItem().split(","));
            }
        }
        return query;
    }

    @Override
    public QueryWrapper<JkExceptionInfo> getQueryP() {
        return query;
    }

}