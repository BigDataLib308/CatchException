package com.jtexplorer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jtexplorer.entity.JkExceptionInfo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 异常信息 Mapper 接口
 * </p>
 *
 */
public interface JkExceptionInfoMapper extends BaseMapper<JkExceptionInfo> {
    /**
     * 检查日志表是否存在
     * @param database
     * @return
     */
    int checkTable(String database);

    /**
     * 创建数据表
     * @return
     */
    int createTable();
}
