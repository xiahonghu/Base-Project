package com.xiahonghu.project.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/index")
public class IndexAction {

    private Logger log = LoggerFactory.getLogger(IndexAction.class);

    @RequestMapping("/hello")
    @SentinelResource(value = "hello", fallback = "helloError")
    public String sentinelTest(String name) {
        log.info("success,你好!!!," + name);
        return "success,你好," + name;
    }

    @RequestMapping("/test")
    public String sentinelTest2() {
        log.info("success,你好!!! test");
        return "success,你好!!! test";
    }

    public String helloError(String name, Throwable e) {
        log.info("限流控制,name:{}",name, e.getMessage());
        return "error," + name;
    }
}
