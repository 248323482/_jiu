package com.jiu.validator.mateconstraint;

import com.jiu.validator.model.ConstraintInfo;

import java.lang.annotation.Annotation;


/**
 * 约束转换器
 *
 */
public interface IConstraintConverter {

    /**
     * 支持的类型
     *
     * @param clazz
     * @return
     */
    boolean support(Class<? extends Annotation> clazz);

    /**
     * 转换
     *
     * @param ano
     * @return
     * @throws Exception
     */
    ConstraintInfo converter(Annotation ano) throws Exception;
}
