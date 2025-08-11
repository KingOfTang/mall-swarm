package com.macro.mall.auth.controller;

import com.macro.mall.auth.domain.Oauth2TokenDto;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.constant.AuthConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义Oauth2获取令牌接口
 * 该 Java 代码实现了一个基于 OAuth2 的自定义 Token 获取接口，用于认证中心的登录认证。其主要功能如下：
 *
 * 1. **接收 OAuth2 相关参数**（如 `grant_type`, `client_id`, [username](file://F:\baiducloud\mall-swarm\mall-mbg\src\main\java\com\macro\mall\model\UmsAdmin.java#L9-L9), [password](file://F:\baiducloud\mall-swarm\mall-mbg\src\main\java\com\macro\mall\model\UmsAdmin.java#L11-L11) 等）；
 * 2. **调用 Spring Security OAuth2 的 TokenEndpoint** 获取标准 `OAuth2AccessToken`；
 * 3. **将 Token 转换为自定义响应格式 [Oauth2TokenDto](file://F:\baiducloud\mall-swarm\mall-auth\src\main\java\com\macro\mall\auth\domain\Oauth2TokenDto.java#L11-L23)**；
 * 4. **返回统一格式的响应 `CommonResult<Oauth2TokenDto>`**。
 *
 * 整体作用：**对外提供一个封装后的 OAuth2 获取 Token 接口，支持多种授权模式（如密码模式、刷新 Token 模式等）。**
 */
@RestController
@Api(tags = "AuthController", description = "认证中心登录认证")
@RequestMapping("/oauth")
public class AuthController {

    @Autowired
    private TokenEndpoint tokenEndpoint;

    /**
     * 用于Oauth2认证流程中获取访问令牌的API接口
     * 该接口允许客户端通过提供授权凭证来请求访问令牌，支持多种授权类型
     *
     * @param request HttpServletRequest对象，包含HTTP请求的所有数据
     * @param grant_type 授权模式，例如password、refresh_token等
     * @param client_id Oauth2客户端ID，用于标识请求访问令牌的客户端
     * @param client_secret Oauth2客户端秘钥，用于验证客户端的身份
     * @param refresh_token 用于获取新访问令牌的刷新令牌，非必需
     * @param username 登录用户名，用于password授权类型
     * @param password 登录密码，用于password授权类型
     * @return 返回一个CommonResult对象，包含Oauth2TokenDto对象，其中有访问令牌信息
     * @throws HttpRequestMethodNotSupportedException 如果请求方法不支持抛出此异常
     */
    @ApiOperation("Oauth2获取token")
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public CommonResult<Oauth2TokenDto> postAccessToken(HttpServletRequest request,
                                                        @ApiParam("授权模式") @RequestParam String grant_type,
                                                        @ApiParam("Oauth2客户端ID") @RequestParam String client_id,
                                                        @ApiParam("Oauth2客户端秘钥") @RequestParam String client_secret,
                                                        @ApiParam("刷新token") @RequestParam(required = false) String refresh_token,
                                                        @ApiParam("登录用户名") @RequestParam(required = false) String username,
                                                        @ApiParam("登录密码") @RequestParam(required = false) String password) throws HttpRequestMethodNotSupportedException {
        // 创建一个参数映射，用于存储所有请求参数
        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type",grant_type);
        parameters.put("client_id",client_id);
        parameters.put("client_secret",client_secret);
        parameters.putIfAbsent("refresh_token",refresh_token);
        parameters.putIfAbsent("username",username);
        parameters.putIfAbsent("password",password);

        // 调用TokenEndpoint中的postAccessToken方法获取访问令牌
        OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(request.getUserPrincipal(), parameters).getBody();

        // 构建Oauth2TokenDto对象，用于存储访问令牌及其相关信息
        Oauth2TokenDto oauth2TokenDto = Oauth2TokenDto.builder()
                .token(oAuth2AccessToken.getValue())
                .refreshToken(oAuth2AccessToken.getRefreshToken().getValue())
                .expiresIn(oAuth2AccessToken.getExpiresIn())
                .tokenHead(AuthConstant.JWT_TOKEN_PREFIX).build();

        // 返回包含访问令牌信息的成功结果
        return CommonResult.success(oauth2TokenDto);
    }
}
