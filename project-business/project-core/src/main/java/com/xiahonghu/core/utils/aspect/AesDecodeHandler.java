package com.xiahonghu.core.utils.aspect;




import com.xiahonghu.core.utils.encrypt.AESEncrypt;
import com.xiahonghu.core.utils.encrypt.JwtUtils;
import com.xiahonghu.core.utils.exception.Assert;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

@RestControllerAdvice
public class AesDecodeHandler extends RequestBodyAdviceAdapter {


    /**
     * 判断是否需要走该接口(post请求带有RequestBody并且没有PlainRes的类所有方法都需要走该请求)
     * @return 返回为true表示需要走这个类的方法
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return methodParameter.hasParameterAnnotation(RequestBody.class) && methodParameter.getContainingClass().getAnnotation(com.xiahonghu.core.utils.aspect.PlainRes.class) == null;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        String encBody = StreamUtils.copyToString(inputMessage.getBody(), Charset.defaultCharset());

        String aesKey = JwtUtils.getAesKey();
        Assert.custom(aesKey==null,401,"AesKey过期");
        String httpBody = AESEncrypt.decrypt(encBody, aesKey);
        return new AesHttpMessage(httpBody == null ? null : new ByteArrayInputStream(httpBody.getBytes()), inputMessage.getHeaders());
    }

}
