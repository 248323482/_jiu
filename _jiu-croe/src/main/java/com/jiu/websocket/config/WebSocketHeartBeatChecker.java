package com.jiu.websocket.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jiu.websocket.TodoAtRemoved;
import com.jiu.websocket.WebSocket;
import com.jiu.websocket.WebSocketManager;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 */
public class WebSocketHeartBeatChecker {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketHeartBeatChecker.class);

    /**
     * 定时检测 WebSocket 的心跳时间跟现在的间隔，超过设定的值说明失去了心跳，就去除他，并更新数据库
     * 基于每次 WebSocket 的心跳都更新其心跳时间
     * @param webSocketManager 要检测的容器
     * @param timeSpan 检查到心跳更新时间大于这么毫秒就认为断开了（心跳时间）
     * @param errorTolerant 容忍没有心跳次数
     * @param todoAtRemoved 在删除的时候额外需要做的事情
     */
    public void check(WebSocketManager webSocketManager ,
                                    long timeSpan ,
                                    int errorTolerant ,
                                    TodoAtRemoved todoAtRemoved) {
        final long timeSpans = timeSpan * errorTolerant;
        Map<String, WebSocket> socketMap = webSocketManager.localWebSocketMap();
        Date now = new Date();
        List<WebSocket> toRemoves = new LinkedList<>();
        socketMap.forEach((identifier , webSocket) -> {
            long interval = now.getTime() - webSocket.getLastHeart().getTime();
            if(interval >= timeSpans){
                //说明失去心跳了
                logger.info("{} 失去心跳了" , identifier);
                toRemoves.add(webSocket);
            }
        });

        if (toRemoves.size() > 0 ){
            for (WebSocket webSocket : toRemoves) {
                //内存删了
                socketMap.remove(webSocket.getIdentifier());
            }

            //额外比如还有数据库操作
            todoAtRemoved.todoWith(toRemoves);
        }
    }
}
