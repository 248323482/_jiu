package com.jiu.log.init;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 通过环境变量的形式注入 logging.file.name
 * 自动维护 Spring Boot Admin Logger Viewer
 *
 */
public class ApplicationLoggerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String logBase = environment.getProperty("logging.file.path", "/data/projects/logs");
        String appName = environment.getProperty("spring.application.name");
        // spring boot admin 直接加载日志
        System.setProperty("logging.file.name", String.format("%s/%s/root.log", logBase, appName));

        // nacos的日志文件路径
        System.setProperty("nacos.logging.path", String.format("%s/%s", logBase, appName));
    }


}
