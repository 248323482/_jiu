package com.jiu.websocket.event;

import com.jiu.websocket.WebSocket;
import org.springframework.context.ApplicationEvent;

/**
 */
public class WebSocketConnectEvent extends ApplicationEvent {
    public WebSocketConnectEvent(WebSocket webSocket){
        super(webSocket);
    }
}
