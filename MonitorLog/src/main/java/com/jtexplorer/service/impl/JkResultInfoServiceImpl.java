package com.jtexplorer.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jtexplorer.entity.JkResultInfo;
import com.jtexplorer.entity.dto.JkResultInfoDTO;
import com.jtexplorer.entity.enums.RequestEnum;
import com.jtexplorer.entity.query.JkResultInfoQuery;
import com.jtexplorer.mapper.ExtJkResultInfoMapper;
import com.jtexplorer.mapper.JkResultInfoMapper;
import com.jtexplorer.service.IJkResultInfoService;;
import com.jtexplorer.utils.EntityUtil;
import com.jtexplorer.utils.JsonResult;
import com.jtexplorer.utils.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 接口返回数据 服务实现类
 *
 */
@Service
public class JkResultInfoServiceImpl extends ServiceImpl<ExtJkResultInfoMapper, JkResultInfo> implements IJkResultInfoService {

    @Autowired
    private JkResultInfoMapper jkResultInfoMapper;

    /**
     * 分页查询接口返回数据
     *
     * @return JsonResult
    */
    @Override
    public JsonResult selectAll(JkResultInfoQuery query) {
        JsonResult jsonResult = new JsonResult();
        query.buildExample();
        Page<JkResultInfo> page = query.buildPage(this);
        List<JkResultInfoDTO> records = EntityUtil.parentListToChildList(page.getRecords(), JkResultInfoDTO.class);
        if(ListUtil.isNotEmpty(records)){
            jsonResult.buildTrueNew(page.getTotal(),records);
        }else {
            jsonResult.buildFalseNew(RequestEnum.REQUEST_ERROR_DATABASE_QUERY_NO_DATA);
        }
        return jsonResult;
    }

    /**
     * 根据主键删除接口返回数据
     *
     * @param id 主键
     * @return JsonResult
    */
    @Override
    public JsonResult deleteByKey(Long id) {
        JsonResult jsonResult = new JsonResult();
        if(removeById(id)){
            jsonResult.buildTrueNew();
        }
        return jsonResult;
    }

    /**
     * 根据主键修改接口返回数据
     *
     * @param record 修改信息
     * @return JsonResult
    */
    @Override
    public JsonResult updateByKey(JkResultInfoDTO record) {
        JsonResult jsonResult = new JsonResult();
        if(updateById(record)){
            jsonResult.buildTrueNew();
        }
        return jsonResult;
    }

    /**
     * 插入接口返回数据
     *
     * @param record 插入信息
     * @return JsonResult
    */
    @Override
    public JsonResult add(JkResultInfoDTO record) {
        JsonResult jsonResult = new JsonResult();
        if(save(record)){
            jsonResult.buildTrueNew();
        }
        return jsonResult;
    }

    /**
     * 检查数据库是否存在
     * @param database 当前连接的数据库名
     */
    @Override
    public void checkTable(String database) {
        if (jkResultInfoMapper.checkTable(database) <= 0) {
            jkResultInfoMapper.createTable();
        }
    }

}
