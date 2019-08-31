package com.jiu.config.enable;


import com.jiu.config.Swagger2;
import com.jiu.config.Swgger2EnumProperty;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * /doc.html
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = { java.lang.annotation.ElementType.TYPE })
@Documented
@Import({ Swagger2DocumentationConfiguration.class, Swagger2.class, Swgger2EnumProperty.class})
public @interface EnableSwagger2 {
}