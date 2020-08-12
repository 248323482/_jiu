package com.jiu.websocket.event;

import com.jiu.websocket.WebSocket;
import org.springframework.context.ApplicationEvent;

/**
 */
public class WebSocketCloseEvent extends ApplicationEvent {
    public WebSocketCloseEvent(WebSocket webSocket){
        super(webSocket);
    }
}
