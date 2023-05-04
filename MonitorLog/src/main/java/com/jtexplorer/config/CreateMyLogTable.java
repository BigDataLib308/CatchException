package com.jtexplorer.config;

import com.jtexplorer.service.IJkExceptionInfoService;
import com.jtexplorer.service.IJkResultInfoService;
import com.jtexplorer.service.ISystemLogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 自动创建数据库表
 */
@Component
public class CreateMyLogTable {

    @Value("${jtConfig.jtLog.databaseName}")
    private String databaseName;

    @Resource
    private ISystemLogService systemLogService;
    @Resource
    private IJkExceptionInfoService jkExceptionInfoService;
    @Resource
    private IJkResultInfoService jkResultInfoService;

    /**
     * 检查表是否存在，不存在则创建，@PostConstruct确保只执行一次
     */
    @PostConstruct
    private void checkTable() {
        systemLogService.checkTable(databaseName.trim());

        jkExceptionInfoService.checkTable(databaseName.trim());

        jkResultInfoService.checkTable(databaseName.trim());
    }
}
