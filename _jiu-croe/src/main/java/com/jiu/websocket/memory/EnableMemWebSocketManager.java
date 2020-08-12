package com.jiu.websocket.memory;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({MemoryWebSocketConfig.class})
public @interface EnableMemWebSocketManager {
}
