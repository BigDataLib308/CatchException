package com.qq.aop.aspect;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jtexplorer.entity.JkExceptionInfo;
import com.jtexplorer.entity.JkResultInfo;
import com.jtexplorer.entity.enums.RequestEnum;
import com.jtexplorer.entity.query.JkExceptionInfoQuery;
import com.jtexplorer.redis.MyLogRedisCache;
import com.jtexplorer.utils.*;
import com.qq.aop.annotation.CatchException;
import com.qq.aop.entity.SystemLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Date;
import java.util.Set;

public class CatchExceptionAspect {

    private Long currentTime = null;
    private String uId = null;
    private String logUserAccount = null;
    private String logUserRealName = null;
    private String platformType = null;
    private long startTime;
    private long endTime;

    private Map<String, String> map = new HashMap<>();

    @Resource
    private MyLogRedisCache myLogRedisCache;
    @Resource
    private WxCpSendMsgUtil wxCpSendMsgUtil;
    @Value("${Config.Log.projectName}")
    private String projectName;
    @Value("${Config.Log.noticeEnable}")
    private String noticeEnable;
    @Value("${Config.Log.file.enable}")
    private String enable;
    @Value("${Config.Log.file.url}")
    private String url;

    /**
     * 切入点标明作用在所有注解的方法上
     */
    @Pointcut("@annotation(com.qq.aop.annotation.CatchException)")
    public void cutPoint() {
    }

    /**
     * 前置操作：目标执行之前执行
     * @param joinPoint
     */
    @Before("cutPoint()")
    public void Before(JoinPoint joinPoint) {
        currentTime = System.currentTimeMillis();
        startTime = System.currentTimeMillis();
    }

    /**
     * 环绕操作：环绕目标方法执行
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("cutPoint()")
    public Object Around(ProceedingJoinPoint joinPoint) throws Throwable {
        SystemLog systemLog = new SystemLog();
        String className = joinPoint.getTarget().getClass().getSimpleName() + "[" + joinPoint.getSignature().getName() + "]";
        systemLog.setSyloClassName(className);
        HttpServletRequest httpServletRequest = MyLogHttpContextUtil.getHttpServletRequest();
        Date date = new Date();
        CatchException sessionLog = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(CatchException.class);
        Object proceed = null;
        if (!sessionLog.sysLog()) {
            try {
                proceed = joinPoint.proceed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String client = sessionLog.client();
            String act = sessionLog.remark();
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
    @After("sendLog()")
    public void doAfter(JoinPoint joinPoint) throws Throwable {

    }

    /**
     * 返回操作：执行方法结束前执行（抛出异常不执行）
     * 接口返回的数据结构
     * 对接口返回的数据结构success状态判读接口请求是否成功
     * 将请求接口返回false的接口信息保存到数据库中
     *
     * @param point
     * @return
     */
    @AfterReturning(returning = "jsonResult", pointcut = "cutPoint()")
    public Object AfterRequest(JoinPoint point, Object jsonResult) throws Exception {
        //获取返回值，解析jsonResult
        String str = JSON.toJSONString(jsonResult);
        JsonRes jsonRes = JSONObject.parseObject(str, JsonRes.class);
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
                    && (!jsonRes.getTip().equals(RequestEnum.REQUEST_ERROR_NO_LOGIN.getCode())
                    && !jsonRes.getTip().equals(RequestEnum.REQUEST_ERROR_DATABASE_QUERY_NO_DATA.getCode())
                    && !jsonRes.getTip().equals(RequestEnum.REQUEST_ERROR_LOGIN_NO_PERMISSION.getCode()))) {
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
     * 切面异常处理
     *
     * @param e
     * @param point
     */
    @AfterThrowing(value = "cutPoint()", throwing = "e")
    public void catchException(JoinPoint point, Exception e) throws Exception {
        CatchException sessionLog = ((MethodSignature) point.getSignature()).getMethod().getAnnotation(CatchException.class);
        if (sessionLog.errorRecord()) {
            JkExceptionInfo jkExceptionInfo = new JkExceptionInfo();
            MethodSignature methodSignature = ((MethodSignature) point.getSignature());
            Method method = methodSignature.getMethod();
            CatchException myLog = method.getAnnotation(CatchException.class);
            jkExceptionInfo.setJkExecMethodRemark(myLog.remark());
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
                logInfo.append("方法描述：" + myLog.remark() + "\n");
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

}
