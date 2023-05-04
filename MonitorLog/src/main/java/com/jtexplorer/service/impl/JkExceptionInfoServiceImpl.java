package com.jtexplorer.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jtexplorer.entity.JkExceptionInfo;
import com.jtexplorer.entity.dto.JkExceptionInfoDTO;
import com.jtexplorer.entity.enums.RequestEnum;
import com.jtexplorer.entity.query.JkExceptionInfoQuery;
import com.jtexplorer.mapper.ExtJkExceptionInfoMapper;
import com.jtexplorer.mapper.JkExceptionInfoMapper;
import com.jtexplorer.service.IJkExceptionInfoService;
import com.jtexplorer.utils.EntityUtil;
import com.jtexplorer.utils.JsonResult;
import com.jtexplorer.utils.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 异常信息 服务实现类
 *
 */
@Service
public class JkExceptionInfoServiceImpl extends ServiceImpl<ExtJkExceptionInfoMapper, JkExceptionInfo> implements IJkExceptionInfoService {

    @Autowired
    private JkExceptionInfoMapper jkExceptionInfoMapper;

    /**
     * 分页查询异常信息
     *
     * @return JsonResult
     */
    @Override
    public JsonResult selectAll(JkExceptionInfoQuery query) {
        JsonResult jsonResult = new JsonResult();
        query.buildExample();
        Page<JkExceptionInfo> page = query.buildPage(this);
        List<JkExceptionInfoDTO> records = EntityUtil.parentListToChildList(page.getRecords(), JkExceptionInfoDTO.class);
        if (ListUtil.isNotEmpty(records)) {
            jsonResult.buildTrueNew(page.getTotal(), records);
        } else {
            jsonResult.buildFalseNew(RequestEnum.REQUEST_ERROR_DATABASE_QUERY_NO_DATA);
        }
        return jsonResult;
    }

    /**
     * 根据主键删除异常信息
     *
     * @param id 主键
     * @return JsonResult
     */
    @Override
    public JsonResult deleteByKey(Long id) {
        JsonResult jsonResult = new JsonResult();
        if (removeById(id)) {
            jsonResult.buildTrueNew();
        }
        return jsonResult;
    }

    /**
     * 根据主键修改异常信息
     *
     * @param record 修改信息
     * @return JsonResult
     */
    @Override
    public JsonResult updateByKey(JkExceptionInfoDTO record) {
        JsonResult jsonResult = new JsonResult();
        if (updateById(record)) {
            jsonResult.buildTrueNew();
        }
        return jsonResult;
    }

    /**
     * 插入异常信息
     *
     * @param record 插入信息
     * @return JsonResult
     */
    @Override
    public JsonResult add(JkExceptionInfoDTO record) {
        JsonResult jsonResult = new JsonResult();
        if (save(record)) {
            jsonResult.buildTrueNew();
        }
        return jsonResult;
    }

    /**
     * 检查数据库是否存在
     *
     * @param database 当前连接的数据库名
     */
    @Override
    public void checkTable(String database) {
        if (jkExceptionInfoMapper.checkTable(database) <= 0) {
            jkExceptionInfoMapper.createTable();
        }
    }

}
