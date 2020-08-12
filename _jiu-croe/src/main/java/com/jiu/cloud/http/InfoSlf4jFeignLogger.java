package com.jiu.cloud.http;

import org.slf4j.Logger;

/**
 */
public class InfoSlf4jFeignLogger extends feign.Logger {
    private final Logger logger;

    public InfoSlf4jFeignLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(String.format(methodTag(configKey) + format, args));
        }
    }

}
