package com.macro.mall.auth.exception;

import com.macro.mall.common.api.CommonResult;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局处理Oauth2抛出的异常
 * 该代码定义了一个全局异常处理器，用于捕获并处理 Spring Security OAuth2 抛出的 `OAuth2Exception` 异常。其功能如下：
 *
 * - 使用 `@ControllerAdvice` 注解对所有控制器进行全局异常拦截；
 * - 通过 `@ExceptionHandler` 拦截 `OAuth2Exception` 类型的异常；
 * - 使用 `@ResponseBody` 将返回值直接作为响应体；
 * - 异常处理方法 [handleOauth2] 返回统一格式的失败结果 [CommonResult.failed(e.getMessage())]。
 */
@ControllerAdvice
public class Oauth2ExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = OAuth2Exception.class)
    public CommonResult handleOauth2(OAuth2Exception e) {
        return CommonResult.failed(e.getMessage());
    }
}
