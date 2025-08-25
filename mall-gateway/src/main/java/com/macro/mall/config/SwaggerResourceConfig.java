package com.macro.mall.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger资源配置
 * 该Java代码用于配置Spring Cloud Gateway中Swagger资源的动态加载，主要功能如下：
 *
 * 1. **扫描所有路由**，获取其ID；
 * 2. **过滤出配置文件中定义的路由**；
 * 3. **提取路径断言（Path Predicate）**，拼接生成对应的`/v2/api-docs`路径；
 * 4. **构造SwaggerResource对象列表**，供Swagger UI展示多个微服务的API文档。
 */
@Slf4j
@Component
@Primary
@AllArgsConstructor
public class SwaggerResourceConfig implements SwaggerResourcesProvider {

    private final RouteLocator routeLocator;
    private final GatewayProperties gatewayProperties;

    /**
     * 获取Swagger资源列表
     *
     * 此方法通过扫描所有路由，并根据预定义的路由属性生成Swagger资源列表
     * 它首先收集所有路由的ID，然后过滤出配置文件中定义的路由
     * 对于每个匹配的路由，它进一步过滤出基于路径的路由断言，并根据这些路径生成Swagger资源
     *
     * @return SwaggerResource对象列表，每个对象代表一个可访问的Swagger API文档资源
     */
    @Override
    public List<SwaggerResource> get() {
        // 初始化Swagger资源列表
        List<SwaggerResource> resources = new ArrayList<>();
        // 初始化路由ID列表
        List<String> routes = new ArrayList<>();
        //获取所有路由的ID
        routeLocator.getRoutes().subscribe(route -> routes.add(route.getId()));
        //过滤出配置文件中定义的路由->过滤出Path Route Predicate->根据路径拼接成api-docs路径->生成SwaggerResource
        gatewayProperties.getRoutes().stream().filter(routeDefinition -> routes.contains(routeDefinition.getId())).forEach(route -> {
            route.getPredicates().stream()
                    .filter(predicateDefinition -> ("Path").equalsIgnoreCase(predicateDefinition.getName()))
                    .forEach(predicateDefinition -> resources.add(swaggerResource(route.getId(),
                            predicateDefinition.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0")
                                    .replace("**", "v2/api-docs"))));
        });

        //返回收集到的Swagger资源列表
        return resources;
    }

    /**
     * 创建一个Swagger资源对象
     * 该方法用于初始化和配置Swagger资源，指定其名称和位置
     *
     * @param name 资源的名称，用于在Swagger UI中显示
     * @param location 资源的位置，通常是Swagger JSON文件的URL
     * @return 返回配置好的SwaggerResource对象
     */
    private SwaggerResource swaggerResource(String name, String location) {
        // 记录资源的名称和位置信息
        log.info("name:{},location:{}", name, location);

        // 创建一个新的SwaggerResource对象
        SwaggerResource swaggerResource = new SwaggerResource();

        // 设置Swagger资源的名称
        swaggerResource.setName(name);
        // 设置Swagger资源的位置
        swaggerResource.setLocation(location);
        // 设置Swagger资源的版本为2.0
        swaggerResource.setSwaggerVersion("2.0");

        // 返回配置好的SwaggerResource对象
        return swaggerResource;
    }
}
