package com.macro.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClientImportSelector;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 启用DiscoveryClient注解
 *
 * 本注解用于启用Spring Cloud的DiscoveryClient功能，使得当前应用能够发现和消费其他服务
 * 它是Spring Cloud中实现服务发现机制的关键注解之一，通过它，应用可以轻松地注册到如Eureka、Consul等服务注册中心
 * 并且能够方便地获取其他服务的信息，从而实现服务间的调用
 */
@EnableDiscoveryClient

@SpringBootApplication
public class MallGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallGatewayApplication.class, args);
    }
}
