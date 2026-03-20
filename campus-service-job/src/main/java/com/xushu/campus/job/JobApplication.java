package com.xushu.campus.job;

import org.mybatis.spring.annotation.MapperScan;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 兼职服务启动类
 */
@SpringBootApplication(
    scanBasePackages = {"com.xushu.campus.job", "com.xushu.campus.common"},
    exclude = {
        MybatisPlusAutoConfiguration.class
    }
)
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.xushu.campus.common.feign"})
@EnableScheduling
@MapperScan("com.xushu.campus.job.mapper")
public class JobApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobApplication.class, args);
    }

}