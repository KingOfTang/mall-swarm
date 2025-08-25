package com.macro.mall.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 配置类，用于存储不需要安全拦截的URL
 * 通过配置前缀"secure.ignore"来加载相应的配置属性
 * 该Java代码定义了一个配置类 [IgnoreUrlsConfig] 列表中，供后续安全配置使用。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Component
@ConfigurationProperties(prefix="secure.ignore")
public class IgnoreUrlsConfig {
    /**
     * 需要忽略的安全检查URL列表
     */
    private List<String> urls;
}
