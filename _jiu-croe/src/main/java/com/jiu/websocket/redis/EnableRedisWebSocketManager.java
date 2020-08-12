package com.jiu.websocket.redis;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({RedisWebSocketConfig.class})
public @interface EnableRedisWebSocketManager {
}
