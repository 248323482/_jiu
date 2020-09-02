package com.jiu.validator.mateconstraint.impl;


import com.jiu.validator.mateconstraint.IConstraintConverter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * 长度 转换器
 *
 */
public class RangeConstraintConverter extends BaseConstraintConverter implements IConstraintConverter {

    @Override
    protected List<String> getMethods() {
        return Arrays.asList("min", "max", "message");
    }

    @Override
    protected String getType(Class<? extends Annotation> type) {
        return "range";
    }

    @Override
    protected List<Class<? extends Annotation>> getSupport() {
        return Arrays.asList(Length.class, Size.class, Range.class);
    }

}
