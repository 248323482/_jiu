package com.jiu.configuration;


import com.jiu.web.context.RegisterUserInterceptor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.charset.Charset;
import java.util.List;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {


    /**
     * 扩展拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //注册校验码
        registry.addInterceptor(new RegisterUserInterceptor()).addPathPatterns("/register.hb");

        WebMvcConfigurer.super.addInterceptors(registry);

    }


    /**
     * 解决乱码问题
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        WebMvcConfigurer.super.configureMessageConverters(converters);
        converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
    }


    /**
     * 国际化, localResolver 配置
     */
    @Bean
    public LocaleResolver localeResolver() {
        return new com.jiu.web.context.LocaleResolver();
    }

    // 国际化配置
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("/WEB-INF/classes/hb");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }


}