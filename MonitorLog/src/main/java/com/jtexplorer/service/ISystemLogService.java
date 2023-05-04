package com.jtexplorer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jtexplorer.entity.SystemLog;
import com.jtexplorer.entity.dto.LogCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface ISystemLogService extends IService<SystemLog> {

    /**
     * 检查数据库是否存在
     * @param database 当前连接的数据库名
     */
    void checkTable(String database);


    List<LogCount> selectLogCount(String startTime, String endTime);


}
