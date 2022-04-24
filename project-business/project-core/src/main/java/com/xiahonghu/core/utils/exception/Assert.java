package com.xiahonghu.core.utils.exception;

public class Assert {
    public static boolean isBlank(String str){
        return str == null || str.isEmpty();
    }

    public static void isBlank(String str, String message) {
        if(isBlank(str)) throw new RRException(message);
    }

    public static boolean isNull(Object object){
        return  object == null;
    }

    public static void isNull(Object object, String message) {
        if(isNull(object)) throw new RRException(message);
    }

    public static void handJudge(Boolean result, String message) {
        if(result) throw new RRException(message);
    }

    public static void custom(Boolean result,Integer code, String message){
        if(result) throw new RRException(message,code);
    }
}
