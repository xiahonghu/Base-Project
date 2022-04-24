package com.xiahonghu.core.utils.exception;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 */
@RestControllerAdvice
public class RRExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(RRException.class)
    public R handleRRException(RRException e) {
        R r = new R();
        r.put("code", 500);
        r.put("msg", e.getMessage());
        logger.error("===> custom error:"+JSONObject.toJSONString(r));
        return r;
    }

    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        logger.error("未知异常:{}", e.getMessage(),e);
        return R.error("未知异常，请联系管理员");
    }
}

