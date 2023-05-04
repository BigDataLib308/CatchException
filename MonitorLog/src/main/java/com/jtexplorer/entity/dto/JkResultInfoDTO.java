package com.jtexplorer.entity.dto;

import com.jtexplorer.entity.JkResultInfo;
import lombok.Data;

/**
 * 接口返回数据 DTO类
 *
*/
@Data
public class JkResultInfoDTO extends JkResultInfo {
    /**
     * 验证添加方法参数空值
     *
     * @return Boolean
     */
    public Boolean verifyAddNullAttribute() {
        return true;
    }

    /**
     * 验证修改方法参数空值
     *
     * @return Boolean
     */
    public Boolean verifyUpdateNullAttribute() {
        return true;
    }
}
