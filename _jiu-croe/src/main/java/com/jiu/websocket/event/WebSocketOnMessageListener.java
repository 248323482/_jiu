package com.jiu.websocket.event;

import com.jiu.websocket.WebSocket;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import java.util.function.Consumer;

@Slf4j
@AllArgsConstructor
public class WebSocketOnMessageListener {

    private Consumer<String> consumer;

    @Async
    @Order
    @EventListener(WebSocketOnMessageEvent.class)
    public void connect(WebSocketOnMessageEvent event) {
        String message = (String) event.getSource();
        consumer.accept(message);
    }

}