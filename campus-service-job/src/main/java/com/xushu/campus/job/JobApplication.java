package com.xushu.campus.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 兼职服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.xushu.campus.job", "com.xushu.campus.common"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.xushu.campus.common.feign"})
@EnableScheduling
public class JobApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobApplication.class, args);
    }

}