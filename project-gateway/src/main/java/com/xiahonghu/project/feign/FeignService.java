package com.xiahonghu.project.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("business-services")
public interface FeignService {

    @GetMapping("/test/test")
    String testPost();
}

