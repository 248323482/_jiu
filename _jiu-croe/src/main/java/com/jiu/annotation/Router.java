package com.jiu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 水平分表注解使用此注解请添加mybatis 拦截器
 * com.faquir.datasource.rout.interceptor.RouteInterceptor
 * @By        九
 * @Date   2019年8月20日
 * @Time   上午10:35:35
 * @Email budongilt@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Router {
	/**
	 * 水平分表属性
	 */
	String routerField() default "";

	/**
	 * 水平分表原始表面,联合查询请写多个
	 */
	String[] tableName() default {};

	/**
	 * 表分割符号
	 */
	String tableStyle() default "_";

}
