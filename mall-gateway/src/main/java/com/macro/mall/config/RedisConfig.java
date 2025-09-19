package com.macro.mall.config;

import com.macro.mall.common.config.BaseRedisConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 这段代码是Spring Boot项目中的Redis配置类，主要功能包括：
 *
 * 1. **继承基础配置**：继承[BaseRedisConfig]获得基础Redis配置
 * 2. **启用Redis仓库**：通过`@EnableRedisRepositories`注解启用Redis数据仓库功能
 * 3. **配置序列化**：虽然未在选中代码中体现，但导入了JSON和字符串序列化器，用于配置RedisTemplate的key-value序列化方式
 *
 * 该配置类为项目提供Redis缓存和数据存储的基础配置。
 */
@Configuration
public class RedisConfig extends BaseRedisConfig {

}
