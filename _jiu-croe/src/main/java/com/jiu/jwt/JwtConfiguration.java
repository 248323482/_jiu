package com.jiu.jwt;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 */
@EnableConfigurationProperties(value = {
        JwtProperties.class,
})
@Component
public class JwtConfiguration {

    @Bean
    public TokenUtil getTokenUtil(JwtProperties authServerProperties) {
        return new TokenUtil(authServerProperties);
    }
}
