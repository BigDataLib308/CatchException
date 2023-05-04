package com.jtexplorer.aop.aspect;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jtexplorer.aop.annotation.CatchException;
import com.jtexplorer.entity.JkExceptionInfo;
import com.jtexplorer.entity.JkResultInfo;
import com.jtexplorer.entity.SystemLog;
import com.jtexplorer.entity.enums.RequestEnum;
import com.jtexplorer.entity.query.JkExceptionInfoQuery;
import com.jtexplorer.redis.MyLogRedisCache;
import com.jtexplorer.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 自定义切面
 */
@Aspect
@Component
@Slf4j
public class CatchExceptionAspect {

    private Long currentTime = null;
    //用户id
    private String uId = null;
    //用户账户
    private String logUserAccount = null;
    //用户真实姓名
    private String logUserRealName = null;
    //平台
    private String platformType = null;
    private long startTime;
    private long endTime;

    /**
     * 缓存配置中的信息
     */
    private Map<String, String> map = new HashMap<>();

    /**
     * 环境配置
     */
    @Resource
    private Environment environment;
    @Resource
    private MyLogRedisCache myLogRedisCache;
    @Resource
    private WxCpSendMsgUtil wxCpSendMsgUtil;


    private String databaseName;

    private String projectName;

    private String noticeEnable;

    private String enable;

    private String url;

    /**
     * 切入点标明作用在所有被JTLog注解的方法上
     */
    @Pointcut("@annotation(com.jtexplorer.aop.annotation.CatchException)")
    public void cutPoint() {
    }

    @Before("cutPoint()")
    public void Before(JoinPoint joinPoint) {
        currentTime = System.currentTimeMillis();
        startTime = System.currentTimeMillis();
    }


    @Around("cutPoint()")
    public Object Around(ProceedingJoinPoint joinPoint) throws Throwable {
        SystemLog systemLog = new SystemLog();
        String className = joinPoint.getTarget().getClass().getSimpleName() + "[" + joinPoint.getSignature().getName() + "]";
        systemLog.setSyloClassName(className);
        HttpServletRequest httpServletRequest = MyLogHttpContextUtil.getHttpServletRequest();
        Date date = new Date();
        CatchException catchException = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(CatchException.class);
        Object proceed = null;
        if (!catchException.sysLog()) {
            try {
                proceed = joinPoint.proceed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String client = catchException.client();
            String act = catchException.remark();

            String reponseData = "";
            HttpSession session = httpServletRequest.getSession();
            String requestURI = httpServletRequest.getRequestURI();
            Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
            if (MyLogStringUtil.isEmpty(map.get("currentIP"))) {
                map.put("currentIP", InetAddress.getLocalHost().getHostAddress());
            }

            try {
                proceed = joinPoint.proceed();
                if (MyLogStringUtil.isNotEmpty(proceed)) {
                    try {
                        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(proceed));
                        reponseData = jsonObject.toJSONString();
                    } catch (JSONException e) {
                        reponseData = JSON.toJSONString(proceed);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                reponseData = e.getMessage();
            }
            this.drawUserInfo(session, client);
            systemLog.setSyloCreateTime(date)
                    .setSyloIpAddress(MyLogIpUtil.getIpAddress(httpServletRequest))
                    .setSyloMethodName(requestURI)
                    .setSyloRequestUrl(map.get("currentIP"))
                    .setSyloDescription(act)
                    .setSyloResponseData(MyLogStringUtil.isEmpty(reponseData) ? null : reponseData)
                    .setSyloRequestTime(System.currentTimeMillis() - currentTime)
                    .setSyloRequestData(MyLogStringUtil.isEmpty(parameterMap) ? null : JSON.toJSONString(parameterMap).replace("[", "").replace("]", ""))
                    .setSyloLoginUserId(uId)
                    .setSyloAccount(logUserAccount)
                    .setSyloUserRealName(logUserRealName)
                    .setSyloClient(platformType);

            if (requestURI.substring(requestURI.lastIndexOf("/") + 1).trim().equals("login")) {
                if (JSON.parseObject(JSON.toJSONString(proceed)).get("success").toString().trim().equals("false")) {
                    systemLog.setSyloLoginUserId(null);
                }
            }
            String dateStr = LogTimeTools.transformDateFormat(systemLog.getSyloCreateTime(), "yyyy-MM-dd HH:mm:ss");
            String redisKey = "systemLog_" + projectName + "_" + systemLog.getSyloIpAddress() + "_" + systemLog.getSyloRequestUrl() + "_" + dateStr;
            myLogRedisCache.setCacheObject(redisKey, systemLog);
        }
        return proceed;
    }

    /**
     * 后置操作:目标方法之后执行（始终执行）
     * @param joinPoint
     * @throws Throwable
     */
    @After("")
    public void doAfter(JoinPoint joinPoint) throws Throwable {

    }

    /**
     * 接口返回的数据结构
     * 对接口返回的数据结构success状态判读接口请求是否成功
     * 将请求接口返回false的接口信息保存到数据库中
     *
     * @param point
     * @return
     */
    @AfterReturning(returning = "jsonResult", pointcut = "cutPoint()")
    public Object AfterRequest(JoinPoint point, Object jsonResult) throws Exception {
        String str = JSON.toJSONString(jsonResult);
        JsonResult jsonRes = JSONObject.parseObject(str, JsonResult.class);
        endTime = System.currentTimeMillis();
        Long costTime = endTime - startTime;
        JkResultInfo jkResultInfo = new JkResultInfo();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        String url = request.getRequestURI();
        jkResultInfo.setJkResuUrl(url);
        String className = point.getTarget().getClass().getSimpleName() + "[" + point.getSignature().getName() + "]";
        jkResultInfo.setJkResuClassName(className);
        jkResultInfo.setJkResuCostTime(costTime);
        if (!jsonRes.isSuccess()) {
            jkResultInfo.setJkResuSuccess(0);
            if (jsonRes.getTip() != null
                    && (!jsonRes.getTip().equals(RequestEnum.ERROR_LOGIN_NOT_LOGIN.getCode())
                    && !jsonRes.getTip().equals(RequestEnum.REQUEST_ERROR_DATABASE_QUERY_NO_DATA.getCode())
                    && !jsonRes.getTip().equals(RequestEnum.ERROR_LOGIN_NO_PERMISSION.getCode()))) {
                jkResultInfo.setJkResuFailReasonShow(jsonRes.getFailReasonShow());
                jkResultInfo.setJkResuFailReason(jsonRes.getFailReason());
            }
        }
        jkResultInfo.setJkResuDbCreateTime(new Date());
        if (costTime > 60 * 1000) {
            jkResultInfo.setJkResuIsOvertime(1);
            String dateStr = LogTimeTools.transformDateFormat(jkResultInfo.getJkResuDbCreateTime(), "yyyy-MM-dd HH:mm:ss");
            String redisKey = "jkResultInfo_" + projectName + "_" + jkResultInfo.getJkResuUrl() + "_" + jkResultInfo.getJkResuCostTime() + "_" + dateStr;
            myLogRedisCache.setCacheObject(redisKey, jkResultInfo);
        } else {
            if (!jsonRes.isSuccess()) {
                if (jsonRes.getTip() != null && (!jsonRes.getTip().equals(RequestEnum.REQUEST_ERROR_DATABASE_QUERY_NO_DATA.getCode())
                        && !jsonRes.getTip().equals(RequestEnum.REQUEST_ERROR_DATABASE_QUERY_NO_DATA.getCode()))) {
                    String dateStr = LogTimeTools.transformDateFormat(jkResultInfo.getJkResuDbCreateTime(), "yyyy-MM-dd HH:mm:ss");
                    String redisKey = "jkResultInfo_" + projectName + "_" + jkResultInfo.getJkResuUrl() + "_" + jkResultInfo.getJkResuCostTime() + "_" + dateStr;
                    myLogRedisCache.setCacheObject(redisKey, jkResultInfo);
                }
            }
        }
        Set<String> stringSet = myLogRedisCache.getKeys("jkResultInfo_" + projectName + "_" + jkResultInfo.getJkResuUrl());
        if (stringSet.size() != 0 && stringSet.size() % 10 == 0) {
            if (!"false".equals(noticeEnable)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("项目名：" + projectName + "\n");
                stringBuilder.append(url + "接口请求超时");
                wxCpSendMsgUtil.sendTextMsg(stringBuilder.toString());
            }
        }
        return jsonResult;
    }

    /**
     * 切面-异常处理
     *
     * @param e
     * @param point
     */
    @AfterThrowing(value = "cutPoint()", throwing = "e")
    public void catchExceptionThrowing(JoinPoint point, Exception e) throws Exception {
        CatchException catchException = ((MethodSignature) point.getSignature()).getMethod().getAnnotation(CatchException.class);
        if (catchException.errorRecord()) {
            JkExceptionInfo jkExceptionInfo = new JkExceptionInfo();
            MethodSignature methodSignature = ((MethodSignature) point.getSignature());
            Method method = methodSignature.getMethod();
            CatchException sessionLog = method.getAnnotation(CatchException.class);
            jkExceptionInfo.setJkExecMethodRemark(sessionLog.remark());
            Object[] args = point.getArgs();
            DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
            String[] parameterNames = discoverer.getParameterNames(method);
            Map<Object, Object> map = new HashMap<>();
            for (int i = 0; i < parameterNames.length; i++) {
                if (!"session".equals(parameterNames[i])) {
                    map.put(parameterNames[i], args[i]);
                }
            }
            String paramJson = JSON.toJSONString(map);
            jkExceptionInfo.setJkExecParamsJson(paramJson);
            String className = point.getTarget().getClass().getSimpleName() + "[" + point.getSignature().getName() + "]";
            jkExceptionInfo.setJkExceClassName(className);
            jkExceptionInfo.setJkExceMessage(e.getMessage());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            String exception = baos.toString();
            String[] excep = exception.split("\r");
            jkExceptionInfo.setJkExecCause(excep[0]);
            StackTraceElement[] traceElements = e.getStackTrace();
            StringBuilder sb = new StringBuilder();
            StringBuilder errorStr = new StringBuilder();
            for (int i = 0; i < traceElements.length; i++) {
                if (traceElements[i].toString().contains("impl") || traceElements[i].toString().contains("Controller")) {
                    sb.append(traceElements[i].toString() + ";");
                    errorStr.append(traceElements[i].toString() + ";" + "\n");
                }
            }
            jkExceptionInfo.setJkExceTrace(sb.toString());
            jkExceptionInfo.setJkExceDbCreateTime(new Date());
            String dateStr = LogTimeTools.transformDateFormat(jkExceptionInfo.getJkExceDbCreateTime(), "yyyy-MM-dd HH:mm:ss");
            String redisKey = "jkExceptionInfo_" + projectName + "_" + jkExceptionInfo.getJkExceClassName() + "_" + jkExceptionInfo.getJkExecMethodRemark() + "_" + dateStr;
            myLogRedisCache.setCacheObject(redisKey, jkExceptionInfo);

            if (!"false".equals(noticeEnable)) {
                StringBuilder stringBuilder = new StringBuilder();
                StringBuilder logInfo = new StringBuilder();
                stringBuilder.append("项目名：" + projectName + "\n");
                logInfo.append("项目名：" + projectName + "\n");
                stringBuilder.append("方法名：" + className + "\n");
                logInfo.append("方法名：" + className + "\n");
                logInfo.append("方法描述：" + sessionLog.remark() + "\n");
                StringBuilder errorTemp = new StringBuilder();
                if (StringUtil.isNotEmpty(jkExceptionInfo.getJkExceMessage())) {
                    errorTemp.append("异常信息：" + jkExceptionInfo.getJkExceMessage() + "\n");
                } else if (StringUtil.isNotEmpty(jkExceptionInfo.getJkExecCause())) {
                    errorTemp.append("异常原因：" + jkExceptionInfo.getJkExecCause() + "\n");
                }
                errorTemp.append(errorStr.toString());
                if (!"false".equals(enable)) {
                    logInfo.append("参数：" + paramJson + "\n");
                    logInfo.append(errorTemp.toString());
                    JkExceptionInfoQuery query = new JkExceptionInfoQuery();
                    query.buildSavePath();
                    query.buildTxtPath();
                    MyFileWriter.writeString(query.getTxtSavePath(), logInfo.toString());
                    String downloadPath = query.getTxtReturnPath();
                    stringBuilder.append("详情下载：" + url + downloadPath + "\n");
                }
                stringBuilder.append("参数：" + paramJson + "\n");
                stringBuilder.append(errorTemp.toString());
                wxCpSendMsgUtil.sendTextMsg(stringBuilder.toString());
            }
        }
    }

    /**
     * 提取用户信息
     *
     * @param session
     */
    private void drawUserInfo(HttpSession session, String client) {
        String sessionKey = null;
        Object attribute = null;

        String mapKey = "jtConfig.jtLog.session." + client + ".logKey";
        String sessionKeyFromMap = map.get(mapKey);
        if (sessionKeyFromMap == null) {
            sessionKey = environment.getProperty(mapKey);
        } else {
            sessionKey = map.get(sessionKeyFromMap);
        }

        if (MyLogStringUtil.isNotEmpty(session)) {
            attribute = session.getAttribute(sessionKey);
            if (MyLogStringUtil.isNotEmpty(attribute)) {
                this.setInfoFromYaml(client, attribute);
            }
        }

    }


    /**
     * 根据不同的平台获取配置
     *
     * @param platform
     */
    private void setInfoFromYaml(String platform, Object attribute) {

        String typeKey = "jtConfig.jtLog.session." + platform + ".clientType";
        String uidKey = "jtConfig.jtLog.session." + platform + ".logId";
        String accountKey = "jtConfig.jtLog.session." + platform + ".logAccount";
        String realNameKey = "jtConfig.jtLog.session." + platform + ".logUserRealName";

        if (map.get(typeKey) == null) {
            map.put(typeKey, environment.getProperty(typeKey));
        }
        if (map.get(uidKey) == null) {
            map.put(uidKey, environment.getProperty(uidKey));
        }
        if (map.get(accountKey) == null) {
            map.put(accountKey, environment.getProperty(accountKey));
        }
        if (map.get(realNameKey) == null) {
            map.put(realNameKey, environment.getProperty(realNameKey));
        }

        platformType = map.get(typeKey);
        String yml_uId = map.get(uidKey);
        String yml_userAccount = map.get(accountKey);
        String yml_userRealName = map.get(realNameKey);

        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(attribute));

        if (MyLogStringUtil.isNotEmpty(yml_uId) && MyLogStringUtil.isNotEmpty(jsonObject.get(yml_uId))) {
            uId = jsonObject.get(yml_uId).toString();//取出用户id
        }
        if (MyLogStringUtil.isNotEmpty(yml_userAccount) && MyLogStringUtil.isNotEmpty(jsonObject.get(yml_userAccount))) {
            logUserAccount = jsonObject.get(yml_userAccount).toString();
        }
        if (MyLogStringUtil.isNotEmpty(yml_userRealName) && MyLogStringUtil.isNotEmpty(jsonObject.get(yml_userRealName))) {
            logUserRealName = jsonObject.get(yml_userRealName).toString();
        }

    }


}
