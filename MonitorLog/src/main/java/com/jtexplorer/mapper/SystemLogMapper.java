package com.jtexplorer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jtexplorer.entity.SystemLog;
import com.jtexplorer.entity.dto.LogCount;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 */
@Repository
public interface SystemLogMapper extends BaseMapper<SystemLog> {

    /**
     * 检查日志表是否存在
     * @param database
     * @return
     */
    int checkTable(String database);

    /**
     * 检查日志表是否存在
     * @return
     */
    List<String> checkTableColumns();

    /**
     * 创建数据表
     * @return
     */
    int createTableSystemLog();

    /**
     * 创建数据表
     * @return
     */
    int tableSystemLogAddColumn(String columnInfo);
    List<LogCount> selectLogCount(String startTime,String endTime);
}
