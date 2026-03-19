package com.xushu.campus.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 通知服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.xushu.campus.notification", "com.xushu.campus.common"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.xushu.campus.common.feign"})
@EnableScheduling
public class NotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }

}