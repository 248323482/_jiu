package com.jiu.config.enable;

import com.jiu.config.DruidConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * /druid
 * 用户名：Jiu
 * 密码：123456
 * @By        九
 * @Date   2019年8月21日
 * @Time   下午2:26:44
 * @Email budongilt@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(value = {DruidConfiguration.class})
public @interface EnableDruidLog {
}