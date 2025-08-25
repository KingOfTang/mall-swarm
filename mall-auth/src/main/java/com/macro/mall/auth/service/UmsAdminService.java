package com.macro.mall.auth.service;

import com.macro.mall.common.domain.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 该代码定义了一个Feign客户端接口[UmsAdminService],用于在微服务中调用名为`mall-admin`的服务。其主要功能是通过HTTP GET请求调用`/admin/loadByUsername`接口，根据用户名加载用户信息（返回[UserDto](file://F:\baiducloud\mall-swarm\mall-common\src\main\java\com\macro\mall\common\dto\UserDto.java#L12-L22)对象）。
 *
 * 简要说明如下：
 *
 * - `@FeignClient("mall-admin")`：指定该接口用于调用服务名为`mall-admin`的微服务。
 * - `@GetMapping("/admin/loadByUsername")`：映射GET请求路径。
 * - `@RequestParam String username`：传递用户名作为请求参数。
 * - 返回值为[UserDto]，即从远程服务获取的用户数据。
 */
@FeignClient("mall-admin")
public interface UmsAdminService {

    @GetMapping("/admin/loadByUsername")
    UserDto loadUserByUsername(@RequestParam String username);
}
