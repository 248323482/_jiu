package com.jiu.websocket.redis.action;

import com.alibaba.fastjson.JSONObject;
import com.jiu.websocket.WebSocket;
import com.jiu.websocket.WebSocketManager;

import java.util.Map;

/**
 * {
 *     "action":"remove",
 *     "identifier":"xxx"
 * }
 * 给webSocket发送消息的action
 */
public class RemoveAction implements Action{
    @Override
    public void doMessage(WebSocketManager manager, JSONObject object) {
        if(!object.containsKey(IDENTIFIER)){
            return;
        }

        String identifier = object.getString(IDENTIFIER);

        Map<String, WebSocket> localWebSocketMap = manager.localWebSocketMap();
        if(localWebSocketMap.containsKey(identifier)){
            localWebSocketMap.remove(identifier);
        }
    }
}
