package com.macro.mall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * 全局跨域配置
 * 注意：前端从网关进行调用时需要配
 * 该代码配置了Spring WebFlux中的全局跨域（CORS）策略，允许所有来源、方法和请求头的跨域请求。具体功能如下：
 *
 * 1. 创建 `CorsConfiguration` 实例并设置允许所有方法、源、头部，并支持携带凭据。
 * 2. 使用 `UrlBasedCorsConfigurationSource` 将配置注册到所有路径 `/**`。
 * 3. 返回一个 `CorsWebFilter` 实例，用于处理跨域请求。
 */
@Configuration
public class GlobalCorsConfig {

    /**
     * 配置跨域请求过滤器
     *
     * 跨域资源共享（CORS）配置是现代Web服务中常见需求此方法主要用于解决前后端分离架构下的跨域问题
     * 通过允许所有来源、所有HTTP方法和所有请求头，此配置提供了一种宽松的跨域策略
     *
     * @return CorsWebFilter 返回配置好的跨域请求过滤器实例
     */
    @Bean
    public CorsWebFilter corsFilter() {
        // 创建一个新的CORS配置实例
        CorsConfiguration config = new CorsConfiguration();

        // 允许所有HTTP方法跨域请求
        config.addAllowedMethod("*");

        // 允许所有来源的跨域请求
        config.addAllowedOriginPattern("*");

        // 允许所有请求头跨域请求
        config.addAllowedHeader("*");

        // 设置是否允许携带凭据的请求
        config.setAllowCredentials(true);

        // 创建一个新的URL路径模式解析器
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());

        // 将CORS配置注册到所有路径
        source.registerCorsConfiguration("/**", config);

        // 返回一个新的CorsWebFilter实例
        return new CorsWebFilter(source);
    }

}
