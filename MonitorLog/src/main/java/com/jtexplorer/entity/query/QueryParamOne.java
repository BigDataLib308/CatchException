package com.jtexplorer.entity.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import lombok.Data;
import org.apache.ibatis.session.RowBounds;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.util.*;

/**
 * QueryParam class
 *
 * @author 苏友朋
 * @date 2019/06/24 09:41
 */
@SuppressWarnings(value = {"rawtypes"})
@Data
public abstract class QueryParamOne<Q extends QueryParamOne, T> {

    // 参数类，前端可以使用param.属性名 的方式向服务器端发送参数
    private T param;

    // 查询用sql条件构造器，buildExample方法会构建该对象
    private QueryWrapper<T> query;
    // 修改用sql条件构造器，buildExample方法会构建该对象
    private UpdateWrapper<T> update;
    /**
     * 分页页数
     */
    private Integer page = 1;
    /**
     * 分页行数（传值为0，则做不分页处理，查询全部数据）
     */
    private Integer limit = 10;
    /**
     * 排序列，用于动态排序
     * 格式：
     * 1、列名 desc(asc),列名 desc(asc)
     * 2、列名,列名
     * 不指定排序方式的列名，将默认使用orderType的排序方式
     */
    private String orderItem;
    /**
     * 排序类型 默认是默认是desc
     */
    private String orderType = "asc";
    /**
     * 是否导出，是否导出excel文件，需要对应导出类的注解配合
     */
    private String isExport;
    /**
     * 导出excel的Sheet名，默认sheet
     */
    private String sheetName = "sheet";
    /**
     * 下划线（驼峰转下划线时使用）
     */
    public static final char UNDERLINE = '_';
    /**
     * 导出时，将T类对象转为想要的类的对象的方法
     * 在queryPage使用的ExcelBoot的convert内部方式调用
     */
    private QueryConvertConsumer<T> convert;
    /**
     * 导出时，将查询出的T类对象的list进行处理的方法
     * 在queryPage使用的ExcelBoot的pageQuery内部方式调用
     * 非导出时，将用在queryJsonResult中
     */
    private QueryConvertListConsumer<T> convertList;
    /**
     * 查询前业务逻辑，queryPage方法会调用
     */
    private QueryBusinessLogicConsumer BusinessLogic;
    /**
     * 保存地址，导出的excel的保存地址
     */
    private String savePath;
    /**
     * 根目录
     * 默认classpath
     */
    private String webappPath = Objects.requireNonNull(com.jtexplorer.entity.query.QueryParamOne.class.getResource("/")).toString().replaceFirst("file:/", "");
//    private String webappPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("/")).getPath();
    /**
     * 导出文件地址，用于返回给前台下载时使用的，前面带'/'
     */
    private String excelReturnPath;
    private String excelSavePath;
    /**
     * 该列用于，业务逻辑中需要用query类生成excel，但是数据来源已经查询完毕（或者必须使用外部数据来源时）时使用，当该属性不为空时，queryPage的excel导出逻辑将不会自动查询，而是直接使用该属性的数据
     */
    private List<T> dataList;
    private String excelSavePathTemp;
    /**
     * 导入excel文件，目前还没有开发完毕，接下来会加入导入文件的处理逻辑
     */
    File fileExcel;
    private File tempDir;
    /**
     * sql关键字，用于orderItem和groupItem的关键字过滤
     */
    private static List<String> sqlKey = new ArrayList<>();

    static {
        sqlKey.add("create ");
        sqlKey.add("select ");
        sqlKey.add("from ");
        sqlKey.add("where ");
        sqlKey.add("distinct ");
        sqlKey.add("all ");
        sqlKey.add("and ");
        sqlKey.add("or ");
        sqlKey.add("not ");
        sqlKey.add(";");
        sqlKey.add("insert ");
        sqlKey.add("into ");
        sqlKey.add("table ");
        sqlKey.add("values ");
        sqlKey.add("group ");
        sqlKey.add("by ");
        sqlKey.add("union ");
        sqlKey.add("intersect ");
        sqlKey.add("except ");
        sqlKey.add("is ");
        sqlKey.add("having ");
        sqlKey.add("join ");
        sqlKey.add("delete ");
        sqlKey.add("update ");
        sqlKey.add("drop ");
        sqlKey.add("alter ");
        sqlKey.add("user ");
        sqlKey.add("password ");
        sqlKey.add("SET ");
        sqlKey.add("grant ");
        sqlKey.add("privileges ");
        sqlKey.add("identified ");
        sqlKey.add("REVOKE ");
        sqlKey.add("FLUSH ");
    }

    /**
     * 根据继承子类声明的T，获取Class对象
     */
    private Class<T> clazz = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    /**
     * 根据继承子类声明的Q，获取Class对象（就是继承子类的Class对象）
     */
    private Class<Q> clazzQ = (Class<Q>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];


    /**
     * 获取mybatis的分页类对象
     * limit为0的不分页
     *
     * @return RowBounds
     */
    public RowBounds getRowBounds() {
        if (getLimit() == 0) {
            return new RowBounds();
        } else {
            return new RowBounds((this.page - 1) * this.limit, this.limit);
        }
    }

    /**
     * 获取mybatisPlus的分页类对象
     * limit为0的不分页
     *
     * @return Page
     */
    public Page<T> getIPage() {
        Page<T> page = new Page<>(getPage(), getLimit());
        // limit为0时，不分页
        if (getLimit() == 0) {
            // mybatisPlus的分页拦截器会判断该值，如果是-1，则不会分页处理
            page.setSize(-1);
        }
        return page;
    }

    /**
     * 验证某参数是否存在（验证是否为Y，是Y的时候返回true）
     *
     * @return Page
     */
    public boolean verifyParamIsY(String param) {
        return "Y".equals(param);
    }

    /**
     * 查询数据
     *
     * @param service 继承了IService的类的对象
     * @return IPage<T>
     */
//    public IPage<T> queryPage(IService<T> service) {
//        if (getBusinessLogic() != null) {
//            // 查询构建查询条件之前，首先运行业务逻辑
//            getBusinessLogic().convert(this);
//        }
//        // 拼装查询参数（属性query）
//        buildExample();
//        // 分页查询
//        return queryDataBuildExample(service);
//    }

    /**
     * 查询数据，用于避免多次运行业务逻辑
     *
     * @param service 继承了IService的类的对象
     * @return IPage<T>
     */
//    public IPage<T> queryDataBuildExample(IService<T> service) {
//        // 拼装查询参数（属性query）
//        buildExample();
//        // 分页查询
//        return queryData(service);
//    }

    /**
     * 查询数据，用于避免多次buildExample
     *
     * @param service 继承了IService的类的对象
     * @return IPage<T>
     */
    public IPage<T> queryDataBusinessLogic(IService<T> service) {
        if (getBusinessLogic() != null) {
            // 查询构建查询条件之前，首先运行业务逻辑
            getBusinessLogic().convert(this);
        }
        // 分页查询
        return queryData(service);
    }

    /**
     * 查询数据，用于避免多次buildExample和运行业务逻辑
     *
     * @param service 继承了IService的类的对象
     * @return IPage<T>
     */
    public IPage<T> queryData(IService<T> service) {
        // 构建mybatisPlus的分页类对象
        IPage<T> page = getIPage();
        // 分页查询
        return service.page(page, getQuery());
    }


//    /**
//     * 查询或导出
//     *
//     * @param service 继承了IService的类的对象
//     * @param clasz   导出类的Class对象，但是不会自动将查询结果转为该类型
//     * @return IPage<T> 出现异常时，返回null，如果是导出，成功时返回new Page<>()，如果不是导出，则返回查询后的IPage对象
//     */
//    public IPage<T> queryPage(IService<T> service, Class clasz) {
//        if (verifyParamIsY(getIsExport())) {
//            // 是导出
//            // 首先构建文件导出地址
//            buildExcelPath();
//            // 构建查询条件
//            buildExample();
//            // 开始导出，下文中的pageSize参数设置为500的原因是，mybatisPlus的分页上限是500，因此这里必须将导出的分页数量也改为500，以避免这个方法因为查询出的数据量（500）小于其默认的3000而终止继续查询。
//            try {
//                ExcelBoot.ExportBuilder(new FileOutputStream(getExcelSavePath()),
//                        getSheetName(), clasz, 500, Constant.DEFAULT_ROW_ACCESS_WINDOW_SIZE, Constant.DEFAULT_RECORD_COUNT_PEER_SHEET, Constant.OPEN_AUTO_COLUM_WIDTH).exportStream(getQuery(), new ExportFunction<QueryWrapper<T>, T>() {
//                    @Override
//                    public List pageQuery(QueryWrapper queryWrapper, int pageNum, int pageSize) {
//                        IPage<T> page;
//                        if (ListUtil.isNotEmpty(getDataList())) {
//                            if (pageNum > 1) {
//                                return null;
//                            }
//                            // 如果dataList属性不为空，则将使用该数据导出，不做查询
//                            page = new Page<>();
//                            page.setRecords(getDataList());
//                            page.setTotal(getDataList().size());
//                        } else {
//                            // 分页查询数据，为了避免查询数据过大，必须分页查询
//                            setLimit(pageSize);
//                            setPage(pageNum);
//                            // 使用只做业务逻辑运行，但是不重新构建查询条件的查询方式，当然，如果需要每次分页都重新构建，则只需要在业务逻辑中加上构建查询条件的语句即可
//                            page = queryDataBusinessLogic(service);
//                        }
//                        if (getConvertList() == null) {
//                            return page.getRecords();
//                        }
//                        // 运行列表转换逻辑
//                        return getConvertList().convert(page);
//                    }
//
//                    @Override
//                    public Object convert(T o) {
//                        // 对象转化
//                        if (getConvert() == null) {
//                            return o;
//                        }
//                        // 转化方法不为空，则返回转化后的对象
//                        return getConvert().convert(o);
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//                // 出现异常时，返回null
//                return null;
//            } finally {
//                // 删除临时文件夹下的全部文件
//                deleteTempFiles();
//            }
//            return new Page<>();
//        } else {
//            if (ListUtil.isNotEmpty(getDataList())) {
//                // 如果dataList属性不为空，则将该数据返回，不做查询
//                Page<T> page = new Page<>();
//                page.setRecords(getDataList());
//                page.setTotal(getDataList().size());
//                return page;
//            } else {
//                // 查询
//                return queryPage(service);
//            }
//        }
//    }
//
//    public void deleteTempFiles() {
//        if (getTempDir() != null) {
//            File[] tempFiles = getTempDir().listFiles();
//            for (File tempFile : tempFiles) {
//                if (tempFile.isFile()) {
//                    FileUtil.deleteFile(tempFile);
//                }
//            }
//        }
//    }

    /**
     * 查询或导出
     *
     * @param service 继承了IService的类的对象
     * @param clasz   导出类的Class对象，但是不会自动将查询结果转为该类型
     * @return IPage<T> 出现异常时，返回null，如果是导出，成功时返回new Page<>()，如果不是导出，则返回查询后的IPage对象，如果convertList属性为空，则将查询过转换为clasz，如果不为空，则使用convertList转化
     */
//    public JsonResult queryJsonResult(IService<T> service, Class clasz) {
//        JsonResult jsonResult = new JsonResult();
//        // 查询或导出数据
//        IPage<T> page = queryPage(service, clasz);
//        if (page == null) {
//            // 查询或导出出错
//            jsonResult.buildFalseNew(RequestEnum.REQUEST_ERROR_DATABASE_QUERY_NO_DATA);
//            return jsonResult;
//        }
//        if (verifyParamIsY(getIsExport())) {
//            // 是导出，则直接返回文件地址
//            jsonResult.buildTrueNew();
//            jsonResult.setTip(getExcelReturnPath());
//        } else {
//            // 不是导出，则将page.getRecords数据转化之后返回
//            if (ListUtil.isNotEmpty(page.getRecords())) {
//                // 返回数据
//                List records;
//                if (getConvertList() != null) {
//                    // 如果不为空，则使用convertList转化
//                    records = getConvertList().convert(page);
//                } else {
//                    // 如果convertList属性为空，则将查询过转换为clasz
//                    records = EntityUtil.parentListToChildList(page.getRecords(), clasz);
//                }
//                jsonResult.buildTrueNew(page.getTotal(), records);
//            } else {
//                jsonResult.buildFalseNew(RequestEnum.REQUEST_ERROR_DATABASE_QUERY_NO_DATA);
//            }
//        }
//        return jsonResult;
//    }

    /**
     * 检查sql关键字
     */
    public static boolean sqlKeyV(String item) {
        for (String sqlKeyItem : sqlKey) {
            if (item.toLowerCase(Locale.ENGLISH).contains(sqlKeyItem)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成排序条件
     */
//    public void buildOrderQuery(QueryWrapper query) {
//        if (StringUtil.isNotEmpty(getOrderItem())) {
//            // 按照逗号 分开各排序列
//            String[] orderItemStr = getOrderItem().split(",");
//            // 逐列处理，携带排序类型的，则使用携带的排序类型，未携带排序类型的，则使用OrderType
//            for (String item : orderItemStr) {
//                String[] items = item.split(" ");
//                String itemType = getOrderType();
//                if (items.length > 1) {
//                    itemType = items[1];
//                }
//                items[0] = items[0].replace(" ", "");
//                if (sqlKeyV(items[0]) && !items[0].contains(" ")) {
//                    // 首先该列是否存在于原始类（数据表）中
//                    Field[] fields = clazz.getDeclaredFields();
//                    boolean have = false;
//                    for (Field field : fields) {
//                        String columnName = QueryTypeEnum.camelToUnderline(field.getName(), 1);
//                        if (buildOrderItem(query, items[0], columnName, itemType)) {
//                            have = true;
//                            break;
//                        }
//                    }
//                    if (!have) {
//                        // 原始类（数据表）中没有，从注解中找
//                        if (clazzQ.isAnnotationPresent(OrderColumn.class)) {
//                            OrderColumn joinExample = clazzQ.getDeclaredAnnotation(OrderColumn.class);
//                            String[] columnNames = joinExample.columnName();
//                            for (String columnName : columnNames) {
//                                if (buildOrderItem(query, items[0], columnName, itemType)) {
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    /**
     * 构建排序条件列
     *
     * @param query      条件构建类对象
     * @param param      排序列名
     * @param columnName 实际允许的排序列名
     * @param itemType   排序类型（默认asc）
     */
    private boolean buildOrderItem(QueryWrapper query, String param, String columnName, String itemType) {
        if (param.equals(columnName)) {
            if ("desc".equals(itemType)) {
                query.orderByDesc(columnName);
            } else {
                query.orderByAsc(columnName);
            }
            return true;
        }
        return false;
    }

    /**
     * 生成excel地址
     */
//    public void buildExcelPath() {
//        //项目的目录
////        buildSavePath();
//        this.savePath = "upload" + "/" + getYearMonthDay() + "/";
//        StringBuilder exportPath = new StringBuilder();
//        StringBuilder exportPathTemp = new StringBuilder();
//        StringBuilder returnPath = new StringBuilder();
//        exportPath.append(webappPath).append(savePath);
//        exportPathTemp.append(webappPath).append("/upload/").append("poiFiles");
//        returnPath.append("/").append(savePath);
//        if (!new File(exportPath.toString()).exists()) {
//            //创建目录
//            if (!new File(exportPath.toString()).mkdirs()) {
//                return;
//            }
//        }
//        exportPath.append(System.currentTimeMillis()).append(".xlsx");
//        returnPath.append(System.currentTimeMillis()).append(".xlsx");
//        setExcelReturnPath(returnPath.toString());
//        setExcelSavePath(exportPath.toString());
//        setExcelSavePathTemp(exportPathTemp.toString());
//        // 临时文件设置
//        tempDir = new File(getExcelSavePathTemp());
//        if (!tempDir.exists()) {
//            tempDir.mkdirs();
//        }
//        TempFile.setTempFileCreationStrategy(new DefaultTempFileCreationStrategy(tempDir));
//    }

    /**
     * 构建根目录
     */
//    public void buildSavePath() {
//        String webappPath = ParamStaticConfig.getWebappPathStatic(ParamStaticConfig.ConfigParamKeyEnum.uploadUrl).toString();
//        if (StringUtil.isNotEmpty(webappPath)) {
//            this.webappPath = webappPath;
//        }
//    }

    /**
     * 根据日期获取年月日
     *
     * @return 格式化的日期
     */
//    private static String getYearMonthDay() {
//        try {
//            return TimeTools.transformDateFormat(new Date(), "yyyy-MM-dd");
//        } catch (ParseException e) {
//            return "1954-10-01";
//        }
//    }

    /**
     * 将MultipartFile转换为属性fileExcel
     */
//    public JsonResult buildImportExcelFile(MultipartFile file) {
//        JsonResult jsonResult = new JsonResult();
//        buildExcelPath();
//        try {
//            String fileName = file.getOriginalFilename();
//            File folder = new File(getWebappPath() + getSavePath());
//            if (!folder.exists()) {
//                if (!folder.mkdirs()) {
//                    return jsonResult.buildFalseNewReturn(RequestEnum.REQUEST_ERROR_SYSTEM_ERROR, "文件路径创建异常");
//                }
//            }
//            File fileNew = new File(getWebappPath() + getSavePath() + File.separator + fileName);
//            if (!fileNew.exists()) {
//                if (!fileNew.createNewFile()) {
//                    return jsonResult.buildFalseNewReturn(RequestEnum.REQUEST_ERROR_SYSTEM_ERROR, "文件创建异常");
//                }
//            }
//            if (StringUtil.isNotEmpty(fileName)) {
//                setFileExcel(FileUtil.multipartFileToFile(file, getWebappPath() + getSavePath() + "/import" + StringUtil.getRandomString(4) + file.getOriginalFilename()));
//                jsonResult.buildTrueNew();
//                return jsonResult;
//            } else {
//                jsonResult.buildFalseNew(RequestEnum.REQUEST_ERROR_PARAMETER_ERROR, "文件异常");
//                return jsonResult;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            jsonResult.buildFalseNew(RequestEnum.REQUEST_ERROR_PARAMETER_ERROR, "文件异常" + e.getMessage());
//            return jsonResult;
//        }
//    }

    /**
     * 根据param和本身进行查询参数构建
     */
//    public QueryWrapper<T> buildExample() {
//        // 在这里new，用于每次调用都能够重新构建查询参数
//        query = new QueryWrapper<>();
//        update = new UpdateWrapper<>();
//        // 首先根据param将全部不为空的项，做成查询参数
//        if (getParam() != null) {
//            Field[] fields = clazz.getDeclaredFields();
//            for (Field field : fields) {
//                try {
//                    // 过滤掉serialVersionUID属性和属性值是空的
//                    if (!"serialVersionUID".equals(field.getName())
//                            && StringUtil.isNotEmpty(clazz.getMethod("get" + QueryTypeEnum.upperFirstLatter(field.getName())).invoke(getParam()) != null))
//                        // 构建
//                        QueryTypeEnum.EQ.buildQuery(field, getParam(), query, update);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        // 然后根据本身的JoinExample注解，将对应的项，做成查询参数
//        Field[] fields = clazzQ.getDeclaredFields();
//        for (Field field : fields) {
//            if (field.isAnnotationPresent(JoinExample.class)) {
//                JoinExample joinExample = field.getDeclaredAnnotation(JoinExample.class);
//                String columnName = joinExample.columnName();
//                joinExample.queryType().buildQuery(field, this, columnName, query, update);
//            }
//        }
//        // 排序
//        buildOrderQuery(query);
//        return query;
//    }

}