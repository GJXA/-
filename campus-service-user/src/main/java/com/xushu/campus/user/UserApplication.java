package com.xushu.campus.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 用户服务启动类
 */
@SpringBootApplication(
    scanBasePackages = {"com.xushu.campus.user", "com.xushu.campus.common"},
    exclude = {
        MybatisPlusAutoConfiguration.class
    }
)
@MapperScan("com.xushu.campus.user.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.xushu.campus.common.feign"})
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

}