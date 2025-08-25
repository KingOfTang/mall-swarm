package com.macro.mall.auth.config;

import com.macro.mall.common.config.BaseSwaggerConfig;
import com.macro.mall.common.domain.SwaggerProperties;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger API文档相关配置
 * 该Java代码用于配置Swagger API文档，功能如下：
 *
 * - 使用`@Configuration`和`@EnableSwagger2`启用Swagger2文档生成。
 * - 继承[BaseSwaggerConfig]并重写[swaggerProperties()]方法，定义了API的基础包路径、标题、描述、作者、版本及是否启用安全等属性。
 * - 通过`BeanPostProcessor`对Swagger的处理器进行后置处理，增强其功能。
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .apiBasePackage("com.macro.mall.auth.controller")
                .title("mall认证中心")
                .description("mall认证中心相关接口文档")
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
