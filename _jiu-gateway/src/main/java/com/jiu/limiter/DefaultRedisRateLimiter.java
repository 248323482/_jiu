package com.jiu.limiter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.support.ConfigurationService;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.validation.Validator;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class DefaultRedisRateLimiter extends RedisRateLimiter {

    Config getDefaultConfig() {
        return super.getConfig().get("defaultFilters");
    }

    public DefaultRedisRateLimiter(ReactiveStringRedisTemplate redisTemplate,
                                   RedisScript<List<Long>> script,
                                   ConfigurationService configurationService) {
        super(redisTemplate, script, configurationService);
    }

    @Override
    public Mono<Response> isAllowed(String routeId, String id) {
        if (null == super.getConfig().get(routeId))
            getConfig().put(routeId, getDefaultConfig());
        return super.isAllowed(routeId, id);
    }
}