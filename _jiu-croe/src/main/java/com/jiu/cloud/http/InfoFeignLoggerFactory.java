package com.jiu.cloud.http;

import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignLoggerFactory;

/**
 */
public class InfoFeignLoggerFactory implements FeignLoggerFactory {
    @Override
    public feign.Logger create(Class<?> type) {
        return new InfoSlf4jFeignLogger(LoggerFactory.getLogger(type));
    }
}
