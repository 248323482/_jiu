package com.jiu.websocket.redis.action;

import com.alibaba.fastjson.JSONObject;
import com.jiu.websocket.WebSocket;
import com.jiu.websocket.WebSocketManager;
import com.jiu.websocket.utils.WebSocketUtil;

/**
 * {
 *     "action":"sendMessage",
 *     "identifier":"xxx",
 *     "message":"xxxxxxxxxxx"
 * }
 */
public class SendMessageAction implements Action{
    @Override
    public void doMessage(WebSocketManager manager, JSONObject object) {
        if(!object.containsKey(IDENTIFIER)){
            return;
        }
        if(!object.containsKey(MESSAGE)){
            return;
        }

        String identifier = object.getString(IDENTIFIER);

        WebSocket webSocket = manager.get(identifier);
        if(null == webSocket){
            return;
        }
        WebSocketUtil.sendMessage(webSocket.getSession() , object.getString(MESSAGE));
    }
}
