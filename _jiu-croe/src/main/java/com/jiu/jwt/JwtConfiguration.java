package com.jiu.jwt;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 */
@EnableConfigurationProperties(value = {
        JwtProperties.class,
})
public class JwtConfiguration {

    @Bean
    public TokenUtil getTokenUtil(JwtProperties authServerProperties) {
        return new TokenUtil(authServerProperties);
    }
}
