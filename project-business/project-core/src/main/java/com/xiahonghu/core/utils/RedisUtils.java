package com.xiahonghu.core.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Author: sir.li
 * email:  lesli2@qq.com
 * Date:   2020/9/15
 */
@Component
public class RedisUtils {

    private static StringRedisTemplate redisTemplate;
    private static ValueOperations<String, String> valueOperations;
    private static SetOperations<String, String> opsForSet;
    private static HashOperations<String, String,String> hashOperations;



    @Autowired
    public void setRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        redisTemplate = stringRedisTemplate;
        valueOperations = stringRedisTemplate.opsForValue();
        opsForSet = stringRedisTemplate.opsForSet();
        hashOperations = stringRedisTemplate.opsForHash();
    }


    public static Set<String> scan(String matchKey) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keysTmp = new HashSet<>();
            Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match("*" + matchKey + "*").count(1000).build());
            while (cursor.hasNext()) keysTmp.add(new String(cursor.next()));
            return keysTmp;
        });
    }

    public static void generateAtomicLong(String key,long value,int expire){
        RedisAtomicLong counter = new RedisAtomicLong(key, Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        counter.set(value);
        if(expire>0) counter.expire(expire, TimeUnit.SECONDS);
    }

    public static long addAtomicLong(String key,Long value) {
        RedisAtomicLong counter = new RedisAtomicLong(key, Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        return counter.addAndGet(value);
    }

    public static long getAtomicLong(String key) {
        RedisAtomicLong counter = new RedisAtomicLong(key, Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        return counter.get();
    }

    public static String getAndSet(String key,Object value, long expire) {
        String oldKey = valueOperations.getAndSet(key,toJson(value));
        if (expire > 0) redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        return oldKey;
    }

    public static void set(String key, Object value) {
        set(key, value, -1);
    }

    public static void set(String key, Object value, long expire) {
        valueOperations.set(key, toJson(value));
        if (expire > 0) redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    public static <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, -1);
    }

    public static <T> T get(String key, Class<T> clazz, long expire) {
        String value = valueOperations.get(key);
        if (expire > 0 && value != null) redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        return fromJson(value, clazz);
    }

    public static boolean delete(String key) {
        Boolean result = redisTemplate.delete(key);
        return result==null? false:result;
    }

    public static Long hashDel(String collectionName, String key) {
        return hashOperations.delete(collectionName,key);
    }

    /**
     * 向set集合里面增加元素
     * @param collectionName 集合名称
     * @param elements 添加的元素
     * @return set集合中添加元素的个数
     */
    public static int addElement(String collectionName,Object... elements){
        String[] arr = new String[elements.length];
        for (int i = 0; i < arr.length; i++) arr[i] = toJson(elements[i]);
        Long num=opsForSet.add(collectionName,arr);
        redisTemplate.expire(collectionName,3600,TimeUnit.SECONDS);
        return num==null ?0:num.intValue();
    }

    /**
     * 从缓存中获取Set集合
     * @param collectionName 集合名称
     * @return 返回SET集合
     */
    public static <T> Set<T> getMembers(String collectionName, Class<T> clazz){
        Set<String> set = opsForSet.members(collectionName);
        if(set==null) set=new HashSet<>();
        return set.parallelStream().map(s -> fromJson(s,clazz)).collect(Collectors.toSet());
    }

    public static void hashSet(String key, String hKey,Object value) {
        hashOperations.put(key,hKey,toJson(value));
    }

    public static <T> T hashGet(String key, String hKey,Class<T> clazz) {
        return fromJson(hashOperations.get(key,hKey),clazz);
    }

    /**
     * 加锁返回加锁失败或成功(默认30秒锁过期)
     *
     * @param key 加锁的键
     * @return boolean
     */
    public static boolean lock(String key) {
        Boolean result = valueOperations.setIfAbsent(key, key);
        boolean realResult = result == null ? false : result;
        if(realResult) redisTemplate.expire(key, 30, TimeUnit.SECONDS);
        return realResult;
    }

    /**
     *
     * 解锁
     * @param key
     */
    public static void unlock(String key) {
        String currentValue = redisTemplate.opsForValue().get(key);

        if (!StringUtils.isEmpty(currentValue)
                && currentValue.equals(key)) {

            redisTemplate.opsForValue().getOperations().delete(key);
        }
    }

    /**
     * Object转成JSON数据
     */
    private static String toJson(Object object) {
        if (object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        return JSONObject.toJSONString(object);
    }

    /**
     * JSON数据，转成Object
     */
    private static <T> T fromJson(String json, Class<T> clazz) {
        if (clazz.getSimpleName().equals("String")) return clazz.cast(json);
        else return JSONObject.parseObject(json, clazz);
    }
}
