package com.macro.mall.authorization;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.macro.mall.common.constant.AuthConstant;
import com.macro.mall.common.domain.UserDto;
import com.macro.mall.config.IgnoreUrlsConfig;
import com.nimbusds.jose.JWSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 鉴权管理器，用于判断是否有资源的访问权限
 *
 * 该代码实现了一个基于 Spring Security 的响应式鉴权管理器 [AuthorizationManager]，用于判断用户是否有权限访问特定资源。主要功能如下：
 *
 * 1. **放行白名单路径和 OPTIONS 请求**：对配置在 [ignoreUrlsConfig] 中的路径或跨域预检请求直接放行。
 * 2. **Token 解析与用户身份校验**：从请求头中解析 JWT Token，提取用户信息 [UserDto]
 * 3. **根据用户类型限制访问路径**：
 *    - 管理员用户（[ADMIN_CLIENT_ID]）只能访问匹配 [ADMIN_URL_PATTERN] 的路径。
 *    - 普通用户（[PORTAL_CLIENT_ID]）不能访问管理端路径。
 * 4. **权限校验**：
 *    - 对于管理端路径，从 Redis 中获取资源所需角色列表。
 *    - 判断当前用户是否具有访问该资源的角色权限。
 *
 * 最终返回 `AuthorizationDecision` 决定是否允许访问。
 */
@Component
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        // 获取当前请求
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        // 获取请求URI
        URI uri = request.getURI();
        // 创建路径匹配器
        PathMatcher pathMatcher = new AntPathMatcher();

        // 检查请求路径是否在白名单中，如果是则直接放行
        List<String> ignoreUrls = ignoreUrlsConfig.getUrls();
        for (String ignoreUrl : ignoreUrls) {
            if (pathMatcher.match(ignoreUrl, uri.getPath())) {
                return Mono.just(new AuthorizationDecision(true));
            }
        }

        // 对应跨域的预检请求直接放行
        if(request.getMethod()==HttpMethod.OPTIONS){
            return Mono.just(new AuthorizationDecision(true));
        }

        // 检查用户体系登录限制
        try {
            // 获取请求中的token
            String token = request.getHeaders().getFirst(AuthConstant.JWT_TOKEN_HEADER);
            if(StrUtil.isEmpty(token)){
                return Mono.just(new AuthorizationDecision(false));
            }
            // 解析token
            String realToken = token.replace(AuthConstant.JWT_TOKEN_PREFIX, "");
            JWSObject jwsObject = JWSObject.parse(realToken);
            String userStr = jwsObject.getPayload().toString();
            UserDto userDto = JSONUtil.toBean(userStr, UserDto.class);

            // 根据用户类型和请求路径决定是否放行
            if (AuthConstant.ADMIN_CLIENT_ID.equals(userDto.getClientId()) && !pathMatcher.match(AuthConstant.ADMIN_URL_PATTERN, uri.getPath())) {
                return Mono.just(new AuthorizationDecision(false));
            }
            if (AuthConstant.PORTAL_CLIENT_ID.equals(userDto.getClientId()) && pathMatcher.match(AuthConstant.ADMIN_URL_PATTERN, uri.getPath())) {
                return Mono.just(new AuthorizationDecision(false));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return Mono.just(new AuthorizationDecision(false));
        }

        // 非管理端路径直接放行
        if (!pathMatcher.match(AuthConstant.ADMIN_URL_PATTERN, uri.getPath())) {
            return Mono.just(new AuthorizationDecision(true));
        }

        // 管理端路径需校验权限
// 从Redis中获取所有资源及其对应的角色映射
Map<Object, Object> resourceRolesMap = redisTemplate.opsForHash().entries(AuthConstant.RESOURCE_ROLES_MAP_KEY);
// 创建一个迭代器来遍历资源角色映射的键
Iterator<Object> iterator = resourceRolesMap.keySet().iterator();
// 初始化一个列表来存储匹配当前URI的资源的所有权限
List<String> authorities = new ArrayList<>();
// 遍历资源角色映射的每个资源路径
while (iterator.hasNext()) {
    // 获取当前迭代的资源路径
    String pattern = (String) iterator.next();
    // 检查当前资源路径是否与请求的URI路径匹配
    if (pathMatcher.match(pattern, uri.getPath())) {
        // 如果匹配，则将该资源对应的所有角色添加到权限列表中
        authorities.addAll(Convert.toList(String.class, resourceRolesMap.get(pattern)));
    }
}

        authorities = authorities.stream().map(i -> AuthConstant.AUTHORITY_PREFIX + i).collect(Collectors.toList());

        // 认证通过且角色匹配的用户可访问当前路径
        return mono
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(authorities::contains)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

}
