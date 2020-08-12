package com.jiu.websocket.redis.action;

import com.alibaba.fastjson.JSONObject;
import com.jiu.websocket.WebSocketManager;

/**
 * do nothing action
 */
public class NoActionAction implements Action{
    @Override
    public void doMessage(WebSocketManager manager, JSONObject object) {
        // do no thing
    }
}
