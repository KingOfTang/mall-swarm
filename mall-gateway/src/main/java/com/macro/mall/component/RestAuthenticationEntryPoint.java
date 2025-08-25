package com.macro.mall.component;

import cn.hutool.json.JSONUtil;
import com.macro.mall.common.api.CommonResult;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;


/**
 * RestAuthenticationEntryPoint 类用于处理未经过身份验证的请求
 * 实现了 ServerAuthenticationEntryPoint 接口，以自定义 Spring Security 的身份验证入口点
 * 当请求需要身份验证而未提供时，此组件将负责发送适当的 HTTP 响应
 *
 * 该代码实现了一个自定义的 Spring Security 未认证请求处理类 [RestAuthenticationEntryPoint]，其主要功能如下：
 *
 * - 当用户访问需要认证的接口但未携带有效身份信息时，[commence] 方法会被触发；
 * - 设置响应状态码为 200（可自定义）；
 * - 返回 JSON 格式的错误提示信息，内容为“未授权”；
 * - 支持跨域请求、禁止缓存；
 * - 响应体通过 `DataBuffer` 异步写入客户端。
 */
@Component
public class RestAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    /**
     * commence 方法处理身份验证异常
     * 当检测到未认证的请求时，此方法会被调用
     * 它负责设置 HTTP 响应的状态码、头信息和体内容，以告知客户端错误详情
     *
     * @param exchange 用于访问和操作Web请求和响应的对象
     * @param e         身份验证异常，提供异常信息
     * @return 返回一个 Mono<Void>，表示异步处理完成
     */
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        // 获取当前交换的响应对象
        ServerHttpResponse response = exchange.getResponse();
        // 设置响应状态码为 OK（200），因为可能在特定场景下需要自定义状态码
        response.setStatusCode(HttpStatus.OK);
        // 设置响应头，指定内容类型为 JSON
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        // 设置响应头，允许跨域请求
        response.getHeaders().set("Access-Control-Allow-Origin","*");
        // 设置响应头，禁止缓存
        response.getHeaders().set("Cache-Control","no-cache");
        // 将 CommonResult.unauthorized 方法返回的结果转换为 JSON 字符串，作为响应体
        String body= JSONUtil.toJsonStr(CommonResult.unauthorized(e.getMessage()));
        // 创建 DataBuffer 对象，用于写入响应体
        DataBuffer buffer =  response.bufferFactory().wrap(body.getBytes(Charset.forName("UTF-8")));
        // 使用 Mono.just(buffer) 返回一个 Flux<DataBuffer>，完成响应的异步写入
        return response.writeWith(Mono.just(buffer));
    }
}

