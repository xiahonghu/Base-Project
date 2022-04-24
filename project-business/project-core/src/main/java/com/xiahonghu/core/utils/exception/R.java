package com.xiahonghu.core.utils.exception;

import org.springframework.http.HttpStatus;

import java.util.HashMap;

/**
 * 返回数据
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public R() {
        put("code", 200);
        put("msg", "success");
    }

    public static R error() {
        R r = new R();
        r.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        r.put("msg", "system error");
        return r;
    }

    public static R error(String msg) {
        R r = new R();
        r.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        r.put("msg", msg);
        return r;
    }

    public static R ok() {
        return new R();
    }

    public static R ok(Object obj) {
        return ok().put("data",obj);
    }

    public static R custom(int code,Object data){
        R r = new R();
        r.put("code", code);
        r.put("data", data);
        return r;
    }

    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}

