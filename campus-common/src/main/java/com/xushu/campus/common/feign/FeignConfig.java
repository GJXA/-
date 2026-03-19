package com.xushu.campus.common.feign;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Feign 客户端配置
 */
@Configuration
public class FeignConfig {

    /**
     * Feign 日志级别
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Feign 请求拦截器（传递请求头）
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            // 获取当前请求
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // 传递所有请求头
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    String headerValue = request.getHeader(headerName);

                    // 跳过 Content-Length，由 Feign 自动计算
                    if (!"content-length".equalsIgnoreCase(headerName)) {
                        template.header(headerName, headerValue);
                    }
                }

                // 特别传递认证相关的头
                String authorization = request.getHeader("Authorization");
                if (authorization != null) {
                    template.header("Authorization", authorization);
                }

                String xUserId = request.getHeader("X-User-Id");
                if (xUserId != null) {
                    template.header("X-User-Id", xUserId);
                }

                String xUserRoles = request.getHeader("X-User-Roles");
                if (xUserRoles != null) {
                    template.header("X-User-Roles", xUserRoles);
                }
            }
        };
    }

}