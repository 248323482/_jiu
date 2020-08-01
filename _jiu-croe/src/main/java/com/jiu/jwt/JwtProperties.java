package com.jiu.jwt;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * 认证服务端 属性
 *
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = JwtProperties.PREFIX)
public class JwtProperties {
    public static final String PREFIX = "authentication";

    /**
     * 过期时间 2h
     */
    private Long expire = 7200L;
    /**
     * 刷新token的过期时间 8h
     */
    private Long refreshExpire = 28800L;

}
