package com.jiu.wait.annoation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Waiting {

    /**
     * 指定时间内不可重复提交,单位毫秒
     */
    int timeout() default 1000;

}
