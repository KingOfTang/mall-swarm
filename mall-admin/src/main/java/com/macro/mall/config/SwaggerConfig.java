package com.macro.mall.config;

import com.macro.mall.common.config.BaseSwaggerConfig;
import com.macro.mall.common.domain.SwaggerProperties;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger API文档相关配置
 *
 * 该Java代码用于配置Swagger API文档，继承自[BaseSwaggerConfig]并启用Swagger2。其功能如下：
 *
 * 1. `@Configuration` 和 `@EnableSwagger2`：启用Swagger2文档生成功能。
 * 2. [swaggerProperties()] 方法定义了Swagger文档的基本属性，如包路径、标题、描述、联系人、版本等。
 * 3. [springfoxHandlerProviderBeanPostProcessor] Bean用于处理Swagger的处理器注册后置操作。
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .apiBasePackage("com.macro.mall.controller")
                .title("mall后台系统")
                .description("mall后台相关接口文档")
                .contactName("macro")
                .version("1.0")
                .enableSecurity(true)
                .build();
    }

    @Bean
    public BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return generateBeanPostProcessor();
    }
}
