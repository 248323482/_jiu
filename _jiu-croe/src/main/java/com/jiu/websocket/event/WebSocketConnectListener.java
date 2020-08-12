package com.jiu.websocket.event;

import com.jiu.log.event.SysLogEvent;
import com.jiu.websocket.WebSocket;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import java.util.function.Consumer;

@Slf4j
@AllArgsConstructor
public class WebSocketConnectListener {

    private Consumer<WebSocket> consumer;

    @Async
    @Order
    @EventListener(WebSocketConnectEvent.class)
    public void connect(WebSocketConnectEvent event) {
        WebSocket webSocket = (WebSocket) event.getSource();
        consumer.accept(webSocket);
    }

}