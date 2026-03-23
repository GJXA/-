package com.xushu.campus.gateway.config;

import com.xushu.campus.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;

/**
 * JWT配置类
 * 确保JwtUtil的静态字段在网关中正确初始化
 */
@Slf4j
@Configuration
public class JwtConfig {

    @Autowired
    private Environment environment;

    @PostConstruct
    public void initJwtUtil() {
        try {
            // 从环境变量中获取JWT配置
            String secret = environment.getProperty("jwt.secret",
                "defaultSecretKeyForJwtTokenGenerationInCampusPlatform");
            String expirationStr = environment.getProperty("jwt.expiration", "86400000");
            String refreshExpirationStr = environment.getProperty("jwt.refresh-expiration", "604800000");

            // 设置到JwtUtil的静态字段
            JwtUtil.setSecretStatic(secret);
            JwtUtil.setExpirationStatic(Long.parseLong(expirationStr));
            JwtUtil.setRefreshExpirationStatic(Long.parseLong(refreshExpirationStr));

            log.info("JWT配置初始化成功: secret={}, expiration={}ms, refreshExpiration={}ms",
                secret.length() > 10 ? secret.substring(0, 10) + "..." : secret,
                expirationStr, refreshExpirationStr);
        } catch (Exception e) {
            log.error("JWT配置初始化失败", e);
            throw new RuntimeException("JWT配置初始化失败", e);
        }
    }
}