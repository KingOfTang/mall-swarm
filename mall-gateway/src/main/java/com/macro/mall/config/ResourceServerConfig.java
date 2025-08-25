package com.macro.mall.config;

import cn.hutool.core.util.ArrayUtil;
import com.macro.mall.authorization.AuthorizationManager;
import com.macro.mall.common.constant.AuthConstant;
import com.macro.mall.component.RestAuthenticationEntryPoint;
import com.macro.mall.component.RestfulAccessDeniedHandler;
import com.macro.mall.filter.IgnoreUrlsRemoveJwtFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 该Java代码为Spring Security配置类，用于配置基于JWT的OAuth2资源服务器安全策略。主要功能如下：
 *
 * 1. **启用WebFlux安全机制**：通过`@EnableWebFluxSecurity`注解启用响应式安全控制。
 * 2. **配置JWT认证**：使用`oauth2ResourceServer().jwt()`启用JWT令牌认证，并指定自定义的[jwtAuthenticationConverter]来解析JWT中的权限信息。
 * 3. **处理认证异常**：设置自定义的认证失败处理器[RestAuthenticationEntryPoint]
 * 4. **处理授权异常**：设置自定义的无权限访问处理器[RestfulAccessDeniedHandler]
 * 5. **白名单路径放行**：从配置中读取无需认证的URL列表，允许匿名访问。
 * 6. **鉴权管理器**：所有其他请求都通过自定义的[AuthorizationManager]进行权限判断。
 * 7. **过滤器顺序控制**：在认证过滤器之前添加[IgnoreUrlsRemoveJwtFilter]对白名单路径移除JWT头信息。
 * 8. **CSRF禁用**：因前后端分离架构通常不需CSRF防护，故将其禁用。
 *
 * 总结：该配置实现了基于JWT的OAuth2资源服务器的安全控制，包括认证、鉴权、异常处理及白名单机制。
 *
 */
@AllArgsConstructor
@Configuration
@EnableWebFluxSecurity
public class ResourceServerConfig {
    private final AuthorizationManager authorizationManager;
    private final IgnoreUrlsConfig ignoreUrlsConfig;
    private final RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final IgnoreUrlsRemoveJwtFilter ignoreUrlsRemoveJwtFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.oauth2ResourceServer().jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter());
        //自定义处理JWT请求头过期或签名错误的结果
        http.oauth2ResourceServer().authenticationEntryPoint(restAuthenticationEntryPoint);
        //对白名单路径，直接移除JWT请求头
        http.addFilterBefore(ignoreUrlsRemoveJwtFilter,SecurityWebFiltersOrder.AUTHENTICATION);
        http.authorizeExchange()
                .pathMatchers(ArrayUtil.toArray(ignoreUrlsConfig.getUrls(),String.class)).permitAll()//白名单配置
                .anyExchange().access(authorizationManager)//鉴权管理器配置
                .and().exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler)//处理未授权
                .authenticationEntryPoint(restAuthenticationEntryPoint)//处理未认证
                .and().csrf().disable();
        return http.build();
    }

    /**
     * 配置JWT认证转换器
     *
     * 该方法定义了一个Bean，用于创建一个将JWT转换为Mono类型的AbstractAuthenticationToken的转换器
     * 主要作用是解析JWT中的权限信息，并将其转换为Spring Security所需的认证令牌
     *
     * @return Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> 返回一个JWT到认证令牌的转换器
     */
    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        // 创建一个JWT权限转换器实例
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 设置权限前缀，以便在权限名称前添加特定前缀
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(AuthConstant.AUTHORITY_PREFIX);
        // 设置权限声明名称，即JWT中存储权限信息的claim名称
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(AuthConstant.AUTHORITY_CLAIM_NAME);

        // 创建一个JWT认证转换器实例
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // 设置JWT认证转换器的JWT权限转换器
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        // 返回一个适应了Reactive编程模型的JWT认证转换器适配器
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
