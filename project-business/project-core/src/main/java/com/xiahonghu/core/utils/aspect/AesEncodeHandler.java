package com.xiahonghu.core.utils.aspect;

import com.alibaba.fastjson.JSONObject;

import com.xiahonghu.core.utils.encrypt.AESEncrypt;
import com.xiahonghu.core.utils.encrypt.JwtUtils;
import com.xiahonghu.core.utils.exception.R;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class AesEncodeHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return methodParameter.getContainingClass().getAnnotation(com.xiahonghu.core.utils.aspect.PlainRes.class) == null;
    }

    @Override
    public Object beforeBodyWrite(Object result, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if(result instanceof R && ((R) result).get("data") != null){
            Object object = ((R) result).get("data");
            String encData;
            String aseKey = JwtUtils.getAesKey();
            if(object instanceof String){
                encData = AESEncrypt.encrypt((String) object, aseKey);
            }else {
                encData = AESEncrypt.encrypt(JSONObject.toJSONString(object), aseKey);
            }
            ((R) result).put("data",encData);
        }
        return result;
    }
}
