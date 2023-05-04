package com.jtexplorer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jtexplorer.entity.JkResultInfo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 接口返回数据 Mapper 接口
 * </p>
 */
public interface JkResultInfoMapper extends BaseMapper<JkResultInfo> {
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
