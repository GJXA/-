package com.xushu.campus.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 商品服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.xushu.campus.product", "com.xushu.campus.common"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.xushu.campus.common.feign"})
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

}