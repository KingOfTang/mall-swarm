package com.macro.mall.filter;

import com.macro.mall.common.constant.AuthConstant;
import com.macro.mall.config.IgnoreUrlsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

/**
 * 白名单路径访问时需要移除JWT请求头
 *
 * 该 Java 代码实现了一个 Spring WebFlux 的 `WebFilter` 过滤器，用于在指定的白名单路径中移除请求头中的 JWT Token，以跳过身份验证。其功能如下：
 *
 * - **功能简述**：
 * 若请求路径匹配配置的白名单 URL，则移除请求头中的 JWT Token，再继续执行后续过滤器链。
 *
 * - **详细逻辑**（如适用）：
 * 1. 获取当前请求对象和 URI 路径；
 * 2. 使用 `AntPathMatcher` 匹配请求路径是否属于白名单；
 * 3. 若匹配成功，清空请求头中的 JWT Token，并重建请求与交换对象；
 * 4. 最后继续执行过滤器链。
 */
@Component
public class IgnoreUrlsRemoveJwtFilter implements WebFilter {
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;
    /**
     * 自定义过滤器方法，用于处理传入的请求和响应交换对象
     * 此过滤器的主要目的是检查请求路径是否在配置的白名单中，如果是，则移除JWT令牌
     * 这是为了避免在特定路径上进行身份验证检查
     *
     * @param exchange 代表当前的请求和响应交换对象，包含请求、响应以及其他相关信息
     * @param chain    代表当前过滤器链，用于执行下一个过滤器或最终处理请求
     * @return Mono<Void> 表示异步处理完成的信号，没有具体的返回值
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        //获取当前请求对象
        ServerHttpRequest request = exchange.getRequest();
        //获取请求的完整URI
        URI uri = request.getURI();
        //创建一个新的路径匹配器实例，用于后续匹配请求路径
        PathMatcher pathMatcher = new AntPathMatcher();

        //获取配置的白名单路径列表
        List<String> ignoreUrls = ignoreUrlsConfig.getUrls();
        //遍历白名单路径列表
        for (String ignoreUrl : ignoreUrls) {
            //检查当前请求路径是否匹配白名单中的路径
            if (pathMatcher.match(ignoreUrl, uri.getPath())) {
                //如果匹配成功，移除请求头中的JWT令牌，并重新构建请求和交换对象
                request = exchange.getRequest().mutate().header(AuthConstant.JWT_TOKEN_HEADER, "").build();
                exchange = exchange.mutate().request(request).build();
                //继续处理链中的下一个过滤器或最终处理请求
                return chain.filter(exchange);
            }
        }
        //如果请求路径不在白名单中，则直接继续处理链中的下一个过滤器或最终处理请求
        return chain.filter(exchange);
    }
}
