package com.xiahonghu.core.utils.aspect;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@SuppressWarnings("all")
public class SysLogAspect {

    private static Logger logger = LoggerFactory.getLogger(SysLogAspect.class);

    @Pointcut("@annotation(com.sipue.business.utils.aspect.SysLog)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLog syslog = method.getAnnotation(SysLog.class);
        String title = syslog.value();
        if (title.equals("")) {
            String className = joinPoint.getTarget().getClass().getName();
            title = className + "." + signature.getName() + "()";
        }
        StringBuffer startSbf = new StringBuffer("===> ");
        startSbf.append(title+"请求开始");
        Object[] args = joinPoint.getArgs();
        if (args.length == 1) {
            startSbf.append(",请求参数:" + JSONObject.toJSONString(args[0]));
        }
        logger.info(startSbf.toString());
        Object result = joinPoint.proceed();
        StringBuffer endSbf = new StringBuffer("<=== ");
        endSbf.append(title+"请求结束");
        endSbf.append(",响应参数:" + JSONObject.toJSONString(result));
        endSbf.append(",请求耗时:" + (System.currentTimeMillis() - beginTime));
        logger.info(endSbf.toString());
        return result;
    }
}
