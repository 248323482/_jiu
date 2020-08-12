package com.jiu.websocket.event;

import com.jiu.websocket.WebSocket;
import org.springframework.context.ApplicationEvent;

/**
 */
public class WebSocketOnMessageEvent extends ApplicationEvent {
    public WebSocketOnMessageEvent(String  message){
        super(message);
    }
}
