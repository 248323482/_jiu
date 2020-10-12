package com.jiu.eache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jiu.eache.properties.CustomCacheProperties;
import com.jiu.eache.repository.CacheRepository;
import com.jiu.eache.repository.CaffeineRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 内存缓存配置
 *
 */
@Slf4j
@ConditionalOnProperty(prefix = CustomCacheProperties.PREFIX, name = "type", havingValue = "CAFFEINE")
@EnableConfigurationProperties({CustomCacheProperties.class})
public class CaffeineAutoConfigure {

    @Autowired
    private CustomCacheProperties cacheProperties;



    /**
     * caffeine 持久库
     *
     * @return the redis repository
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheRepository redisRepository() {
        return new CaffeineRepositoryImpl();
    }

    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        Caffeine caffeine = Caffeine.newBuilder()
                .recordStats()
                .initialCapacity(500)
                .expireAfterWrite(cacheProperties.getDef().getTimeToLive())
                .maximumSize(cacheProperties.getDef().getMaxSize());
        cacheManager.setAllowNullValues(cacheProperties.getDef().isCacheNullValues());
        cacheManager.setCaffeine(caffeine);

        //配置了这里，就必须事先在配置文件中指定key 缓存才生效
//        Map<String, CustomCacheProperties.Redis> configs = cacheProperties.getConfigs();
//        Optional.ofNullable(configs).ifPresent((config)->{
//            cacheManager.setCacheNames(config.keySet());
//        });
        return cacheManager;
    }

}