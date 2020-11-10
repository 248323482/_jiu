package com.jiu.security.config;

import com.jiu.security.aspect.AuthAspect;
import com.jiu.security.auth.AuthFun;
import com.jiu.security.feign.UserResolverService;
import com.jiu.security.properties.ContextProperties;
import com.jiu.security.properties.SecurityProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 权限认证配置类
 *
 */
@Order
@AllArgsConstructor
@EnableConfigurationProperties({SecurityProperties.class, ContextProperties.class})
public class SecurityConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = SecurityProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public AuthAspect authAspect(AuthFun authFun) {
        return new AuthAspect(authFun);
    }

    @Bean("fun")
    @ConditionalOnMissingBean(AuthFun.class)
    public AuthFun getAuthFun(UserResolverService userResolverService) {
        return new AuthFun(userResolverService);
    }
}
