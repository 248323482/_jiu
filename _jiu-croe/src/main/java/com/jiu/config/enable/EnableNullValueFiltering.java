package com.jiu.config.enable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jiu.config.NullValueFilteringConfig;
import org.springframework.context.annotation.Import;



@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(value = {NullValueFilteringConfig.class})
public @interface EnableNullValueFiltering {

}
