package com.jtexplorer.service.impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jtexplorer.entity.SystemLog;
import com.jtexplorer.entity.dto.LogCount;
import com.jtexplorer.mapper.SystemLogMapper;
import com.jtexplorer.service.ISystemLogService;
import com.jtexplorer.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 */
@Service
public class SystemLogServiceImpl extends ServiceImpl<SystemLogMapper, SystemLog> implements ISystemLogService {


    @Override
    public void checkTable(String database) {
        if (baseMapper.checkTable(database) <= 0) {
            baseMapper.createTableSystemLog();
        }
        List<String> columns = baseMapper.checkTableColumns();
        Field[] fs = SystemLog.class.getDeclaredFields();
        for(Field f:fs){
            if(!f.getName().equals("serialVersionUID")){
                TableField tf = f.getAnnotation(TableField.class);
                if(tf == null || tf.exist()){
                    String name = StringUtil.camelToUnderline(f.getName(),1);
                    if(!columns.contains(name)){
                        String columnInfo = buildColumnInfo(name,f.getType().getName());
                        baseMapper.tableSystemLogAddColumn(columnInfo);
                    }
                }
            }
        }
    }

    @Override
    public List<LogCount> selectLogCount(String startTime, String endTime) {
        return baseMapper.selectLogCount(startTime,endTime);
    }

    private String buildColumnInfo(String columnName,String type){
        if("long".equals(type) || "java.lang.Long".equals(type)){
            return columnName+" "+"bigint(0)";
        }else if("java.util.Date".equals(type)){
            return columnName+" "+"VARCHAR(255)";
        }else if("java.lang.String".equals(type)){
            return columnName+" "+"timestamp(0)";
        }
        return null;
    }

}
