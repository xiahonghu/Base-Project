package com.xiahonghu.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class ApiConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 设置允许跨域的路径
                .allowedOrigins("*")  // 设置允许跨域请求的域名
                .allowCredentials(true) // 是否允许证书 不再默认开启
                .allowedMethods("*") // 设置允许的方法
                .maxAge(3600);// 跨域允许时间
    }
}
