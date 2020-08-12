package com.jiu.websocket.memory;

import com.jiu.websocket.WebSocket;
import com.jiu.websocket.event.WebSocketCloseEvent;
import com.jiu.websocket.event.WebSocketConnectEvent;
import com.jiu.websocket.WebSocketManager;
import com.jiu.websocket.event.WebSocketOnMessageEvent;
import com.jiu.websocket.utils.SpringContextHolder;
import com.jiu.websocket.utils.WebSocketUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class MemWebSocketManager implements WebSocketManager {
    /**
     * 因为全局只有一个 WebSocketManager ，所以才敢定义为非static
     */
    private final Map<String, WebSocket> connections = new ConcurrentHashMap<>(100);

    @Override
    public WebSocket get(String identifier) {
        return connections.get(identifier);
    }

    @Override
    public void put(String identifier, WebSocket webSocket) {
        connections.put(identifier, webSocket);
        //发送连接事件
        SpringContextHolder.getApplicationContext().publishEvent(new WebSocketConnectEvent(webSocket));
    }

    @Override
    public void remove(String identifier) {
        WebSocket removedWebSocket = connections.remove(identifier);
        //发送关闭事件
        if (null != removedWebSocket) {
            SpringContextHolder.getApplicationContext().publishEvent(new WebSocketCloseEvent(removedWebSocket));
        }
    }


    @Override
    public Map<String, WebSocket> localWebSocketMap() {
        return connections;
    }

    @Override
    public void sendMessage(String identifier, String message) {
        WebSocket webSocket = get(identifier);
        if (null == webSocket) {
            throw new RuntimeException("identifier 不存在");
        }

        WebSocketUtil.sendMessage(webSocket.getSession(), message);
    }

    @Override
    public void broadcast(String message) {
        localWebSocketMap().values().forEach(
                webSocket -> WebSocketUtil.sendMessage(
                        webSocket.getSession(), message));
    }

    @Override
    public void onMessage(String identifier, String message) {
        SpringContextHolder.getApplicationContext().publishEvent(new WebSocketOnMessageEvent(message));
    }
}
