package com.jiu.injection.core;

import com.jiu.injection.annonation.InjectionField;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 */
@Data
@AllArgsConstructor
public class FieldParam {
    /**
     * 当前字段上的注解
     */
    private InjectionField injection;
    /**
     * 从当前字段的值构造出的调用api#method方法的参数
     */
    private Serializable queryKey;
    /**
     * 当前字段的具体值
     */
    private Object fieldValue;
}
