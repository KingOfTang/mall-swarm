package com.macro.mall.auth.component;

import com.macro.mall.auth.domain.SecurityUser;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT内容增强器
 * 该代码实现了一个JWT内容增强器，用于在OAuth2令牌中添加自定义用户信息（如用户ID和客户端ID），具体逻辑如下：
 *
 * 1. 从认证对象中获取用户信息（SecurityUser）。
 * 2. 创建一个Map存储用户ID和客户端ID。
 * 3. 将这些信息添加到OAuth2访问令牌的附加信息中。
 */
/**
 * JWT令牌增强器，用于在生成OAuth2访问令牌时添加额外的用户信息
 * 该类实现了TokenEnhancer接口，以增强令牌的功能
 */
@Component
public class JwtTokenEnhancer implements TokenEnhancer {
    /**
     * 增强OAuth2访问令牌的方法
     * 该方法会在生成访问令牌时被调用，用于添加额外的用户信息到令牌中
     *
     * @param accessToken    OAuth2访问令牌对象，代表生成的令牌
     * @param authentication OAuth2认证对象，包含用户认证信息
     * @return 增强后的OAuth2访问令牌
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        // 获取用户信息
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        // 创建一个映射，用于存储要添加到JWT中的额外信息
        Map<String, Object> info = new HashMap<>();
        //把用户ID设置到JWT中
        info.put("id", securityUser.getId());
        // 设置客户端ID到JWT中
        info.put("client_id",securityUser.getClientId());
        // 将额外信息设置到访问令牌中
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        // 返回增强后的访问令牌
        return accessToken;
    }
}

