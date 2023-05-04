package com.jtexplorer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jtexplorer.entity.JkExceptionInfo;
import com.jtexplorer.entity.dto.JkExceptionInfoDTO;
import com.jtexplorer.entity.query.JkExceptionInfoQuery;
import com.jtexplorer.utils.JsonResult;

/**
 * 异常信息 服务类
 *
*/
public interface IJkExceptionInfoService extends IService<JkExceptionInfo> {
 /**
 * 分页查询异常信息
 *
 * @return JsonResult
 */
 JsonResult selectAll(JkExceptionInfoQuery query);

 /**
 * 根据主键删除异常信息
 *
 * @param id 主键
 * @return JsonResult
 */
 JsonResult deleteByKey(Long id);

 /**
 * 根据主键修改异常信息
 *
 * @param record 修改信息
 * @return JsonResult
 */
 JsonResult updateByKey(JkExceptionInfoDTO record);

 /**
 * 插入异常信息
 *
 * @param record 插入信息
 * @return JsonResult
 */
 JsonResult add(JkExceptionInfoDTO record);

 /**
  * 检查数据库是否存在
  * @param database 当前连接的数据库名
  */
 void checkTable(String database);
}
