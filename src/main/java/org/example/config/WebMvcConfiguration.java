package org.example.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.interceptor.JwtTokenAdminInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    private final JwtTokenAdminInterceptor jwtTokenAdminInterceptor;


    public void addCorsMappings(CorsRegistry registry) {
        log.info("开始配置跨域...");
        registry.addMapping("/**")
                // 设置允许的域名，不能使用*，因为withCredentials=true
                .allowedOriginPatterns("http://localhost:5173")
                .allowCredentials(true) // 允许携带凭证
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                // 暴露自定义响应头，用于传递验证码UUID
                .exposedHeaders("X-Captcha-UUID");
    }

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/login")
                .excludePathPatterns("/api/captcha");
    }
}
