package com.macro.mall.filter;

import cn.hutool.core.util.StrUtil;
import com.macro.mall.common.constant.AuthConstant;
import com.nimbusds.jose.JWSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.ParseException;

/**
 * 将登录用户的JWT转化成用户信息的全局过滤器
 * 该Java代码实现了一个Spring Cloud Gateway的全局过滤器[AuthGlobalFilter]，其功能如下：
 *
 * 1. **从请求头中提取JWT令牌**；
 * 2. **解析JWT令牌并获取用户信息**；
 * 3. **将用户信息以新的请求头形式加入HTTP请求**，供后续服务使用；
 * 4. **若无令牌或解析失败，则直接放行请求**。
 *
 * 该过滤器优先级为0，确保在请求处理链中最早执行。
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static Logger LOGGER = LoggerFactory.getLogger(AuthGlobalFilter.class);

    /**
     * 自定义网关过滤器，用于处理每个请求
     * 此过滤器的主要作用是检查请求中是否包含有效的JWT令牌，并从中提取用户信息
     * 如果请求中没有令牌或者令牌解析失败，则不做任何处理，直接放行请求
     * 如果令牌解析成功，则将用户信息添加到请求头中，以便下游服务使用
     *
     * @param exchange 服务器Web交换对象，包含请求和响应的所有信息
     * @param chain 网关过滤器链，用于执行下一个过滤器或最终的路由处理
     * @return 返回一个Mono<Void>，表示过滤器处理完成，请求可以继续进行
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 从请求头中获取JWT令牌
        String token = exchange.getRequest().getHeaders().getFirst(AuthConstant.JWT_TOKEN_HEADER);
        // 如果令牌为空，则直接放行请求
        // 1、支持匿名访问：系统中存在一些不需要登录就能访问的接口，如登录、注册、首页展示等公开接口
        // 2、避免误拦截：对于没有携带token的请求，不进行认证处理，让后续的鉴权管理器(AuthorizationManager)来判断是否需要权限验证
        // 3、职责分离：该过滤器主要负责解析和传递用户信息，而不是进行权限判断，具体的权限控制由专门的鉴权管理器处理
        if (StrUtil.isEmpty(token)) {
            return chain.filter(exchange);
        }
        try {
            // 移除令牌前缀，获取真实的令牌字符串
            String realToken = token.replace(AuthConstant.JWT_TOKEN_PREFIX, "");
            // 解析JWT令牌
            JWSObject jwsObject = JWSObject.parse(realToken);
            // 获取令牌中的用户信息
            String userStr = jwsObject.getPayload().toString();
            // 记录用户信息日志
            LOGGER.info("AuthGlobalFilter.filter() user:{}",userStr);
            // 将用户信息添加到请求头中
            ServerHttpRequest request = exchange.getRequest().mutate().header(AuthConstant.USER_TOKEN_HEADER, userStr).build();
            // 更新交换对象，以包含新的请求头
            exchange = exchange.mutate().request(request).build();
        } catch (ParseException e) {
            // 如果令牌解析失败，则打印异常信息
            e.printStackTrace();
        }
        // 继续处理下一个过滤器或最终的路由处理
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
