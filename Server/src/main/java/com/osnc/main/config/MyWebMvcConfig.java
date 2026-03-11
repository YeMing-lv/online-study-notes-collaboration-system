package com.osnc.main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {

    @Value("${image.path}")
    private String imagePath;

    //废弃的跨域配置，不能用于WebSocket的跨域
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // 所有接口
//                .allowCredentials(true) // 是否发送 Cookie
//                .allowedOrigins("http://localhost:5173") // 支持域
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 支持方法
//                .allowedHeaders("*")
//                .maxAge(3600);
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 访问地址：如http://localhost:8082/image/user/text.png
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:///" + imagePath);
    }

}
