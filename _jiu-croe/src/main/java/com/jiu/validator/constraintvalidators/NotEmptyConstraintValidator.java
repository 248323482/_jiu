package com.jiu.validator.constraintvalidators;


import com.jiu.base.validation.IValidatable;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForCharSequence;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotEmpty;

/**
 * 自定义一个验证 NotEmpty 的校验器。自定义类需要实现IValidatable接口
 *

 */
public class NotEmptyConstraintValidator implements ConstraintValidator<NotEmpty, IValidatable> {

    private NotEmptyValidatorForCharSequence notEmptyValidator = new NotEmptyValidatorForCharSequence();

    @Override
    public void initialize(NotEmpty parameters) {
        notEmptyValidator.initialize(parameters);
    }

    @Override
    public boolean isValid(IValidatable value, ConstraintValidatorContext constraintValidatorContext) {
        return value != null && value.value() != null && !"".equals(value.value());
    }
}
