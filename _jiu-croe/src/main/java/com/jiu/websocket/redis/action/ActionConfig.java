package com.jiu.websocket.redis.action;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 */
@Configuration
@Import({SendMessageAction.class , BroadCastAction.class , RemoveAction.class , NoActionAction.class})
public class ActionConfig {
}