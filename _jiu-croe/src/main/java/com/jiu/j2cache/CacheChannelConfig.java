package com.jiu.j2cache;
 
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2CacheBuilder;
import net.oschina.j2cache.J2CacheConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
/**
 * channel配置
 *
 **/
@Configuration
@ConditionalOnBean(J2cacheAutoConfig.class)
public class CacheChannelConfig {
 
    private final J2CacheConfig j2CacheConfig;
    public CacheChannelConfig (J2CacheConfig j2CacheConfig) {
        this.j2CacheConfig = j2CacheConfig;
    }
 
    @Bean
    public CacheChannel cacheChannel() {
        return J2CacheBuilder.init(j2CacheConfig).getChannel();
    }
 
}