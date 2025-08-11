package com.macro.mall.auth.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * 获取RSA公钥接口
 * 该代码实现了一个Spring Boot控制器，用于提供RSA公钥信息。其功能如下：
 *
 * - 通过`/rsa/publicKey` GET接口获取RSA公钥；
 * - 将公钥封装为JWT JWK格式（JSON Web Key）并以JSON形式返回。
 *
 * 简要流程：
 * 1. 从Spring注入的`KeyPair`中获取公钥；
 * 2. 使用Nimbus JOSE库构建JWK对象；
 * 3. 转换为JSON响应返回给客户端。
 */
@RestController
@Api(tags = "KeyPairController", description = "获取RSA公钥接口")
@RequestMapping("/rsa")
public class KeyPairController {

    @Autowired
    private KeyPair keyPair;

    @GetMapping("/publicKey")
    public Map<String, Object> getKey() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        return new JWKSet(key).toJSONObject();
    }

}
