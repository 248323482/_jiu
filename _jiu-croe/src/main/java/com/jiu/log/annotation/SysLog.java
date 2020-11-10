package com.jiu.log.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    /**
     * 是否启用 操作日志
     *
     * @return
     */
    boolean enabled() default true;

    /**
     * 描述
     *
     * @return {String}
     */
    String value() default "";

    /**
     * 是否拼接Controller类上的描述值
     * @return
     */
    boolean controllerApiValue() default true;

    /**
     * 记录执行参数
     *
     * @return
     */
    boolean request() default true;

    /**
     * 当 request = false时， 方法报错记录请求参数
     *
     * @return
     */
    boolean requestByError() default true;

    /**
     * 记录返回参数
     *
     * @return
     */
    boolean response() default true;
}
