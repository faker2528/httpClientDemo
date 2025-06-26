package org.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 设置允许的域名，不能使用*，因为withCredentials=true
                .allowedOriginPatterns("http://localhost:5173")
                .allowCredentials(true) // 允许携带凭证
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                // 暴露自定义响应头，用于传递验证码UUID
                .exposedHeaders("X-Captcha-UUID");
    }
}