package com.jiu.j2cache;
 
import net.oschina.j2cache.CacheChannel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
 
/**
 * j2cache集成spring cache注解使用
 *
 **/
@Configuration
@ConditionalOnBean(CacheChannel.class)
@EnableCaching
public class SpringCacheConfig extends CachingConfigurerSupport {
 
    private final CacheChannel cacheChannel;
 
    public SpringCacheConfig(CacheChannel cacheChannel) {
        this.cacheChannel = cacheChannel;
    }
 
    @Override
    public CacheManager cacheManager() {
        return new J2CacheSpringCacheManageAdapter(cacheChannel, true);
    }
 
}