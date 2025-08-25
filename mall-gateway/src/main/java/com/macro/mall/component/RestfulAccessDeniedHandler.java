package com.macro.mall.component;

import cn.hutool.json.JSONUtil;
import com.macro.mall.common.api.CommonResult;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;



/**
 * 自定义RESTful访问拒绝处理器
 * 该处理器用于处理当用户请求访问但权限不足时的情况
 */
@Component
public class RestfulAccessDeniedHandler implements ServerAccessDeniedHandler {

    /**
     * 处理访问拒绝的情况
     * 当用户没有足够的权限访问请求的资源时，这个方法会被调用
     * 它会设置响应的状态码为OK（200），并返回一个JSON格式的禁止访问错误信息
     * 同时，它也会设置响应头，允许跨域访问，并禁止缓存
     *
     * @param exchange 服务器与客户端之间请求和响应的交换对象
     * @param denied 访问拒绝异常，包含拒绝访问的原因
     * @return 返回一个空的Mono对象，表示处理完成
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        // 获取响应对象，用于设置响应的状态码和头信息
        ServerHttpResponse response = exchange.getResponse();
        // 设置响应状态码为OK，即使实际上访问被拒绝，这是为了防止暴露敏感信息
        response.setStatusCode(HttpStatus.OK);
        // 设置响应内容类型为JSON
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        // 设置允许跨域访问
        response.getHeaders().set("Access-Control-Allow-Origin","*");
        // 禁止缓存，确保响应不会被错误地缓存
        response.getHeaders().set("Cache-Control","no-cache");
        // 将禁止访问的错误信息转换为JSON格式的字符串
        String body= JSONUtil.toJsonStr(CommonResult.forbidden(denied.getMessage()));
        // 将JSON字符串包装为DataBuffer对象，以便响应对象处理
        DataBuffer buffer =  response.bufferFactory().wrap(body.getBytes(Charset.forName("UTF-8")));
        // 使用Mono.just包装buffer，表示这是一个单一值的异步序列，并写入响应
        return response.writeWith(Mono.just(buffer));
    }
}

