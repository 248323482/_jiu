package com.jiu.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限注解 用于检查请求者是否拥有改URI接口的权限。
 *
 * 注意事项：
 * 1. 开启URI权限时，必须启动Oauth服务
 * 2. 通过swagger直接通过后台服务(如：zuihou-authority-server、zuihou-file-server)调试时，必须在请求头中传递 userid 参数。 否则会提示 无权限
 * 3. 通过swagger直接通过网关服务(如：zuihou-gateway-server、zuihou-zuul-server)调试时，必须在请求头中传递 token 参数 （因为token中含有userid，在网关会将userid解析出来封装到请求头）。否则会提示 无权限。
 * 4. 本地调试时，可以通过全局配置禁用整个服务的 URI权限 校验：zuihou.security.enabled = false
 * 5. 本地调试时，@PreAuth(enabled=false) 禁用单个 Controller 类的URI权限
 *
 * <p>
 * 注解优先级：
 * 子类方法上的注解 > 子类类上的注解 > 父类方法上的注解
 * <p>
 * 特别地：必须在子类类上注解上写上 replace 参数用于替换父类上的{}占位符，否则权限验证始终无法通过
 * <p>
 * 如：
 * \@PreAuth(replace="user")
 * public class UserController extends SuperCacheController { }
 * <p>
 * 则：UserController 的所有CRUD方法均分别需要 user:add、user:update、user:delete、user:view 等权限
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PreAuth {

    /**
     * 是否启用 URI权限拦截
     *
     * @return
     */
    boolean enabled() default true;

    /**
     * Spring el
     * 文档地址：https://docs.spring.io/spring/docs/4.3.16.RELEASE/spring-framework-reference/htmlsingle/#expressions-operators-logical
     */
    String value() default "permit()";

    /**
     * 替换父类@PreAuth注解中value的占位符{}
     *
     * @return
     */
    String replace() default "";
}

