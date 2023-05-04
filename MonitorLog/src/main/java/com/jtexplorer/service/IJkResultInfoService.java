package com.jtexplorer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jtexplorer.entity.JkResultInfo;
import com.jtexplorer.entity.dto.JkResultInfoDTO;
import com.jtexplorer.entity.query.JkResultInfoQuery;
import com.jtexplorer.utils.JsonResult;

/**
 * 接口返回数据 服务类
 *
*/
public interface IJkResultInfoService extends IService<JkResultInfo> {
 /**
 * 分页查询接口返回数据
 *
 * @return JsonResult
 */
 JsonResult selectAll(JkResultInfoQuery query);

 /**
 * 根据主键删除接口返回数据
 *
 * @param id 主键
 * @return JsonResult
 */
 JsonResult deleteByKey(Long id);

 /**
 * 根据主键修改接口返回数据
 *
 * @param record 修改信息
 * @return JsonResult
 */
 JsonResult updateByKey(JkResultInfoDTO record);

 /**
 * 插入接口返回数据
 *
 * @param record 插入信息
 * @return JsonResult
 */
 JsonResult add(JkResultInfoDTO record);

 /**
  * 检查数据库是否存在
  * @param database 当前连接的数据库名
  */
 void checkTable(String database);
}
