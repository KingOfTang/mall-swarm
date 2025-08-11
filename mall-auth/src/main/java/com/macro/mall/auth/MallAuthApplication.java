package com.macro.mall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 该Java类是Spring Boot应用程序的入口类，主要功能如下：
 *
 * 1. `@SpringBootApplication`：启用Spring Boot自动配置和组件扫描，扫描包为`com.macro.mall`。
 * 2. `@EnableDiscoveryClient`：启用服务注册与发现功能。
 * 3. `@EnableFeignClients`：启用Feign客户端，用于实现服务间通信。
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.macro.mall")
public class MallAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallAuthApplication.class, args);
    }

}
