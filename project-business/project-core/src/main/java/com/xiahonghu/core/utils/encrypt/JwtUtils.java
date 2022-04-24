package com.xiahonghu.core.utils.encrypt;



import com.xiahonghu.core.config.Constants;
import com.xiahonghu.core.utils.RedisUtils;
import com.xiahonghu.core.utils.exception.Assert;
import com.xiahonghu.core.utils.exception.RRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);


//    private static BmwUserMapper bmwUserMapper;
//    @Resource
//    public void setMcUserMapper(BmwUserMapper bmwUserMapper) {
//        JwtUtils.bmwUserMapper = bmwUserMapper;
//    }

    /**
     * 获取请求的上下文
     * String deviceId = request.getHeader("bearer");
     * @return HttpServletRequest
     */
    public static HttpServletRequest getContextHttpServletRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        return ((ServletRequestAttributes) attributes).getRequest();
    }

    /**
     * 获取AES对称加密公钥
     * @return aesKey
     */
    public static String getAesKey() {
//        return RedisUtils.get(REDIS_DEVICE_PREFIX+getDeviceId(),String.class,REDIS_DEVICE_VALID_TIME);
        return Constants.AES_KEY;
    }

    public static Object makePublicId() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }


    public static String getDeviceId() {
        HttpServletRequest request = getContextHttpServletRequest();
        String deviceId = request.getHeader("bearer");
        Assert.isBlank(deviceId, "bearer不存在");
        return deviceId;
    }

    public static Integer getUserId(){
        HttpServletRequest request = getContextHttpServletRequest();
        String token = request.getHeader("bearer");
        Assert.handJudge(token==null||token.length()<4,"bearer不存在");
        String strKey = SecretUtils.decrypt(token, Constants.SALT);
        if(Assert.isBlank(strKey)) throw new RRException("bearer不存在",402);
        Integer userId = Integer.valueOf(strKey);
        Map<Integer,String> bearerMap = RedisUtils.get(Constants.REDIS_BEARER_MAP,Map.class);

        Assert.custom(bearerMap==null,402,"bearer过期，请重新登录");
        Assert.custom(!token.equals(bearerMap.get(userId)),402,"bearer过期，请重新登录");

//        BmwUser bmwUser = bmwUserMapper.selectById(userId);
//        Assert.custom(bmwUser ==null,402,"无效的用户");

        return userId;
    }

    public static String strKey(Integer tenKey){
        String bearer = SecretUtils.expiryEncrypt(String.valueOf(tenKey), Constants.SALT, Constants.expire);
        Map<Integer,String> bearerMap = RedisUtils.get(Constants.REDIS_BEARER_MAP,Map.class);
        if (bearerMap==null){
            bearerMap = new HashMap<>();
        }
        bearerMap.put(tenKey,bearer);
        RedisUtils.set(Constants.REDIS_BEARER_MAP,bearerMap);
        return bearer;
    }
}
