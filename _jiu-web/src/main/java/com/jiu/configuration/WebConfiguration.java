package com.jiu.configuration;



import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.concurrent.TimeUnit;


@Configuration
public class WebConfiguration implements ErrorPageRegistrar {


    //设置错误页面
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        //404 错误码的配置
        registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/static/error/404.html"));

    }

}
