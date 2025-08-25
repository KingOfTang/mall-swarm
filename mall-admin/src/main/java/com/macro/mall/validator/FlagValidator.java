package com.macro.mall.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 用户验证状态是否在指定范围内的注解
 * 该代码定义了一个自定义的 Java Bean Validation 注解 [@FlagValidator]，用于验证字段或参数的值是否在指定的范围内。
 *
 * 解释如下：
 *
 * 1. **功能**：该注解用于标记需要验证的字段或参数，确保其值存在于预设的合法值列表中。
 * 2. **关联验证类**：通过 `@Constraint(validatedBy = FlagValidatorClass.class)` 指定具体的验证逻辑由 [FlagValidatorClass] 实现。
 * 3. **属性说明**：
 *    - `String[] value()`：定义允许的取值列表（如 {"A", "B"}）。
 *    - `String message()`：验证失败时返回的默认错误信息。
 *    - `Class<?> groups()` 和 `Class<? extends Payload> payload()` 是标准的分组和负载配置，通常使用默认值即可。
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Constraint(validatedBy = FlagValidatorClass.class)
public @interface FlagValidator {
    String[] value() default {};

    String message() default "flag is not found";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
