package com.jiu.web.config;

import com.jiu.web.config.properties.FileServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author  jiu
 * 图片本地静态资源映射
 */
@Configuration
public class FileResourceConfiguration implements WebMvcConfigurer {
    @Autowired
    private FileServerProperties fileServerProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/**").addResourceLocations("file:" + fileServerProperties.getStoragePath());

    }
}
