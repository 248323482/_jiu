package com.jiu.web.config;

import com.jiu.boot.config.BaseConfig;
import com.jiu.websocket.event.WebSocketOnMessageListener;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class AuthWebConfiguration extends BaseConfig {

}