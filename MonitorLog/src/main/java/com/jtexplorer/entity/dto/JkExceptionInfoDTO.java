package com.jtexplorer.entity.dto;

import com.jtexplorer.entity.JkExceptionInfo;
import lombok.Data;

/**
 * 异常信息 DTO类
 *
*/
@Data
public class JkExceptionInfoDTO extends JkExceptionInfo {
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
