package com.xiahonghu.project.controller;

import com.xiahonghu.project.feign.FeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class FallBackController {


    @Autowired
    private FeignService feignService;

    /**
     * @ClassName FallbackController
     * @Desc TODO   网关断路器
     * @Date 2019/6/23 19:35
     * @Version 1.0
     */
    @RequestMapping("/fallback")
    public String fallback() {
        return "I'm Spring Cloud Gateway fallback.";
    }


    @RequestMapping("/test")
    public String test() {
        return feignService.testPost();
    }
}
