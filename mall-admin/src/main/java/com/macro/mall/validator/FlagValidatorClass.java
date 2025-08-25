package com.macro.mall.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 状态标记校验器
 * 该 Java 代码实现了一个自定义的 Bean Validation 校验器，用于校验整型（`Integer`）状态值是否符合指定的合法值集合。具体功能如下：
 *
 * 1. **功能说明**：
 *    [FlagValidatorClass] 的具体实现类，用于验证传入的整型值是否在预设的合法字符串数组 [values] 中。
 *
 * 2. **关键方法**：
 *    - [initialize()]：初始化时将注解中配置的值（字符串数组）保存到成员变量 [values]。
 *    - [isValid()]：校验逻辑，判断传入的整数值是否存在于 [values] 中，支持 null 值通过（视为合法）。
 */
public class FlagValidatorClass implements ConstraintValidator<FlagValidator,Integer> {
    private String[] values;
    @Override
    public void initialize(FlagValidator flagValidator) {
        this.values = flagValidator.value();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = false;
        if(value==null){
            //当状态为空时使用默认值
            return true;
        }
        for(int i=0;i<values.length;i++){
            if(values[i].equals(String.valueOf(value))){
                isValid = true;
                break;
            }
        }
        return isValid;
    }
}
