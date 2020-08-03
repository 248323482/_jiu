package com.jiu.j2cache;
 
import net.oschina.j2cache.J2CacheConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
import java.util.Properties;
 
/**
 * J2CacheConfig配置
 *
 **/
@Configuration
@ConditionalOnProperty(prefix = "codingfly.j2cache", name = "enable", havingValue = "true", matchIfMissing = true)
public class CacheConfig {
 
    private final J2cacheProperties j2cacheProperties;
 
    private final RedisProperties redisProperties;
 
    public CacheConfig(J2cacheProperties j2cacheProperties, RedisProperties redisProperties) {
        this.j2cacheProperties = j2cacheProperties;
        this.redisProperties = redisProperties;
    }
 
    @Bean
    public J2CacheConfig j2CacheConfig() {
        J2CacheConfig j2CacheConfig = new J2CacheConfig();
        j2CacheConfig.setBroadcast(j2cacheProperties.getBroadcast());
        j2CacheConfig.setL1CacheName(j2cacheProperties.getL1cache().getName());
        j2CacheConfig.setL2CacheName(j2cacheProperties.getL2cache().getName());
        j2CacheConfig.setSerialization(j2cacheProperties.getSerialization());
        j2CacheConfig.setSyncTtlToRedis(j2cacheProperties.isSyncTtlToRedis());
        j2CacheConfig.setDefaultCacheNullObject(j2cacheProperties.isCacheNullObject());
        j2CacheConfig.setBroadcastProperties(getBroadcastProperties());
        j2CacheConfig.setL1CacheProperties(getL1CacheProperties());
        j2CacheConfig.setL2CacheProperties(getBroadcastProperties());
        return j2CacheConfig;
    }
 
    private Properties getBroadcastProperties() {
        Properties broadcastProperties = new Properties();
        broadcastProperties.setProperty("namespace", "");
        //storage的另一个配置broadcastProperties.setProperty("storage", "hash")
        broadcastProperties.setProperty("storage", "generic");
        broadcastProperties.setProperty("channel", "j2cache");
        broadcastProperties.setProperty("scheme", "redis");
        broadcastProperties.setProperty("hosts", redisProperties.getHost() + ":" + redisProperties.getPort());
        broadcastProperties.setProperty("password", "");
        broadcastProperties.setProperty("database", String.valueOf(redisProperties.getDatabase()));
        broadcastProperties.setProperty("sentinelMasterId", "");
        broadcastProperties.setProperty("maxTotal", "100");
        broadcastProperties.setProperty("maxIdle", "10");
        broadcastProperties.setProperty("minIdle", "10");
        broadcastProperties.setProperty("timeout", "1000");
        return broadcastProperties;
    }
 
    private Properties getL1CacheProperties() {
        Properties l1CacheProperties = new Properties();
        l1CacheProperties.setProperty("region.default", "1000, 30m");
        j2cacheProperties.getCaffeine().forEach(data ->
                l1CacheProperties.setProperty("region." + data.getKey(), data.getValue()));
        return l1CacheProperties;
    }
}