package com.jtexplorer.entity.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jtexplorer.entity.JkResultInfo;
import com.jtexplorer.entity.dto.JkResultInfoDTO;
import com.jtexplorer.utils.StringUtil;
import lombok.Data;

/**
 * 接口返回数据 查询类
 *
 * @author lqq
 * @since 2022-09-22
*/
@Data
public class JkResultInfoQuery extends QueryParamLog {
    /**
     * 原类
     */
    private JkResultInfoDTO param;
    private QueryWrapper<JkResultInfo> query;
    private UpdateWrapper<JkResultInfo> update;
    private String isHaveBlob;

    // likeParam
    // 失败原因
    private String jkResuFailReasonIsLike;
    // 失败原因（用户可见）
    private String jkResuFailReasonShowIsLike;
    // 接口类名
    private String jkResuClassNameIsLike;
    // 备注
    private String jkResuRemarkIsLike;
    // 接口地址
    private String jkResuUrlIsLike;

    public QueryWrapper<JkResultInfo> buildExample() {
        query = new QueryWrapper<>();
        update = new UpdateWrapper<>();
            if(getParam() == null){
                setParam(new JkResultInfoDTO());
            }


    // 
        if(StringUtil.isNotEmpty(getParam().getJkResuId())){
            query.eq("jk_resu_id",getParam().getJkResuId());
            update.eq("jk_resu_id",getParam().getJkResuId());
        }

    // 接口请求是否成功  0：否   1：是
        if(StringUtil.isNotEmpty(getParam().getJkResuSuccess())){
            query.eq("jk_resu_success",getParam().getJkResuSuccess());
            update.eq("jk_resu_success",getParam().getJkResuSuccess());
        }

    // 失败原因
        if(StringUtil.isNotEmpty(getParam().getJkResuFailReason())){
            query.eq("jk_resu_fail_reason",getParam().getJkResuFailReason());
            update.eq("jk_resu_fail_reason",getParam().getJkResuFailReason());
        }
        if(StringUtil.isNotEmpty(getJkResuFailReasonIsLike())){
            query.like("jk_resu_fail_reason",getJkResuFailReasonIsLike());
            update.like("jk_resu_fail_reason",getJkResuFailReasonIsLike());
        }

    // 失败原因（用户可见）
        if(StringUtil.isNotEmpty(getParam().getJkResuFailReasonShow())){
            query.eq("jk_resu_fail_reason_show",getParam().getJkResuFailReasonShow());
            update.eq("jk_resu_fail_reason_show",getParam().getJkResuFailReasonShow());
        }
        if(StringUtil.isNotEmpty(getJkResuFailReasonShowIsLike())){
            query.like("jk_resu_fail_reason_show",getJkResuFailReasonShowIsLike());
            update.like("jk_resu_fail_reason_show",getJkResuFailReasonShowIsLike());
        }

    // 接口类名
        if(StringUtil.isNotEmpty(getParam().getJkResuClassName())){
            query.eq("jk_resu_class_name",getParam().getJkResuClassName());
            update.eq("jk_resu_class_name",getParam().getJkResuClassName());
        }
        if(StringUtil.isNotEmpty(getJkResuClassNameIsLike())){
            query.like("jk_resu_class_name",getJkResuClassNameIsLike());
            update.like("jk_resu_class_name",getJkResuClassNameIsLike());
        }

    // 接口请求耗时
        if(StringUtil.isNotEmpty(getParam().getJkResuCostTime())){
            query.eq("jk_resu_cost_time",getParam().getJkResuCostTime());
            update.eq("jk_resu_cost_time",getParam().getJkResuCostTime());
        }

    // 接口请求是否超时 0：否  1：是
        if(StringUtil.isNotEmpty(getParam().getJkResuIsOvertime())){
            query.eq("jk_resu_is_overtime",getParam().getJkResuIsOvertime());
            update.eq("jk_resu_is_overtime",getParam().getJkResuIsOvertime());
        }

    // 状态   0:未发送告警通知   1：已发送告警通知
        if(StringUtil.isNotEmpty(getParam().getJkResuStatus())){
            query.eq("jk_resu_status",getParam().getJkResuStatus());
            update.eq("jk_resu_status",getParam().getJkResuStatus());
        }

    // 备注
        if(StringUtil.isNotEmpty(getParam().getJkResuRemark())){
            query.eq("jk_resu_remark",getParam().getJkResuRemark());
            update.eq("jk_resu_remark",getParam().getJkResuRemark());
        }
        if(StringUtil.isNotEmpty(getJkResuRemarkIsLike())){
            query.like("jk_resu_remark",getJkResuRemarkIsLike());
            update.like("jk_resu_remark",getJkResuRemarkIsLike());
        }

    // 数据创建时间
        if(StringUtil.isNotEmpty(getParam().getJkResuDbCreateTime())){
            query.eq("jk_resu_db_create_time",getParam().getJkResuDbCreateTime());
            update.eq("jk_resu_db_create_time",getParam().getJkResuDbCreateTime());
        }

    // 数据更新时间
        if(StringUtil.isNotEmpty(getParam().getJkResuDbUpdateTime())){
            query.eq("jk_resu_db_update_time",getParam().getJkResuDbUpdateTime());
            update.eq("jk_resu_db_update_time",getParam().getJkResuDbUpdateTime());
        }

    // 是否删除   1是
        if(StringUtil.isNotEmpty(getParam().getJkResuIsDelete())){
            query.eq("jk_resu_is_delete",getParam().getJkResuIsDelete());
            update.eq("jk_resu_is_delete",getParam().getJkResuIsDelete());
        }

    // 接口地址
        if(StringUtil.isNotEmpty(getParam().getJkResuUrl())){
            query.eq("jk_resu_url",getParam().getJkResuUrl());
            update.eq("jk_resu_url",getParam().getJkResuUrl());
        }
        if(StringUtil.isNotEmpty(getJkResuUrlIsLike())){
            query.like("jk_resu_url",getJkResuUrlIsLike());
            update.like("jk_resu_url",getJkResuUrlIsLike());
        }
// 排序
        if(StringUtil.isNotEmpty(getOrderItem())){
            if(StringUtil.isEmpty(getOrderType())){
                query.orderByAsc(getOrderItem().split(","));
            }else if("asc".equals((getOrderType()))){
                query.orderByAsc(getOrderItem().split(","));
            }else if("desc".equals((getOrderType()))){
                query.orderByDesc(getOrderItem().split(","));
            }
        }
        return query;
    }

    @Override
    public QueryWrapper<JkResultInfo> getQueryP() {
        return query;
    }

}