package com.jtexplorer.entity.dto;

import com.jtexplorer.utils.StringUtil;
import lombok.Data;

@Data
public class LogCount {
    private String url;
    private Integer count;
    private Integer failCount;
    private Integer errorCount;

    public Integer getSuccess(){
        return subUtil(count,getUnSuccess());
    }
    public Integer getUnSuccess(){
        return addUtil(failCount,errorCount);
    }

    public void add(LogCount logCount){
        if(logCount != null){
            count = addUtil(count,logCount.getCount());
            failCount = addUtil(failCount,logCount.getFailCount());
            errorCount = addUtil(errorCount,logCount.getErrorCount());
        }

    }

    private static Integer addUtil(Integer one,Integer two){
        if(StringUtil.isEmpty(one)){
            one = 0;
        }
        if(StringUtil.isEmpty(two)){
            two = 0;
        }
        return one+two;
    }
    private static Integer subUtil(Integer one,Integer two){
        if(StringUtil.isEmpty(one)){
            one = 0;
        }
        if(StringUtil.isEmpty(two)){
            two = 0;
        }
        return one-two;
    }
}
