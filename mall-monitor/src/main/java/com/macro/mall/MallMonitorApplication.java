package com.macro.mall;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 该 Java 类是一个 Spring Boot 应用的启动类，具备以下功能：
 *
 * 1. `@EnableDiscoveryClient`：启用服务发现功能，可注册到如 Eureka、Nacos 等注册中心。
 * 2. `@EnableAdminServer`：启用 Spring Boot Admin 服务端功能，用于监控和管理微服务。
 * 3. `@SpringBootApplication`：标注为 Spring Boot 应用，自动扫描配置和 Bean。
 * 4. [main] 方法通过 `SpringApplication.run` 启动应用。
 */
@EnableDiscoveryClient
@EnableAdminServer
@SpringBootApplication
public class MallMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallMonitorApplication.class, args);
    }

}
