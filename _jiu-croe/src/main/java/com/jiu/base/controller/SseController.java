package com.jiu.base.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.jiu.log.annotation.SysLog;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
public interface SseController <Entity, Id extends Serializable, PageDTO> extends PageController<Entity, PageDTO> {
    static Map<String, SseEmitter> sseCache = new ConcurrentHashMap<>();
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订阅ID", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "events", value = "订阅任务", dataType = "array", paramType = "query")
    })
    @ApiOperation(value = "SSE订阅", notes = "SSE订阅")
    @SysLog("'订阅:' + #id")
    @GetMapping(path = "subscribe")
    @CrossOrigin(origins = "*")
    default SseEmitter push(String id,String[] events) throws  Exception{
        // 超时时间设置为1小时
        SseEmitter sseEmitter = new SseEmitter(60*3*1000L);
        sseCache.put(id, sseEmitter);
        //超时
        sseEmitter.onTimeout(() -> sseTimeOutCall(id) );
        //初始化数据组装
        sseEmitter.send(builder(events,id));
        sseEmitter.onCompletion(() -> sseSuccessCall(id));
        //心跳线程
        heartbeat(sseEmitter,id);
        return sseEmitter;
    }

    @GetMapping( "push")
    @ApiIgnore
    default void push(String id, String content) throws IOException {
        SseEmitter sseEmitter = sseCache.get(id);
        if (sseEmitter != null) {
            sseEmitter.send(content);
        }
    }

    @GetMapping("success")
    @ApiIgnore
    default String success(String id) {
        SseEmitter sseEmitter = sseCache.get(id);
        if (sseEmitter != null) {
            sseEmitter.complete();
        }
        return "over";
    }


    /**
     * seeclient客户端心跳检测
     * @param sseEmitter
     */
    default void heartbeat(SseEmitter sseEmitter,String id){
        CompletableFuture.runAsync(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (true&&sseCache.get(id)!=null){
                    Thread.sleep(2000);
                    SseEmitter.SseEventBuilder builder = SseEmitter
                            .event()
                            .comment("heartbeat");
                    try {
                        sseEmitter.send(builder);
                    }catch (Exception e){
                        //心跳检测发送失败，清楚数据
                        sseCache.remove(id);
                    }
                }
            }
        });
    }

    /**
     * 连接超时回调
     * @param
     * @param id
     */
    default  void  sseTimeOutCall(String id){
        SseEmitter sseEmitter = sseCache.get(id);
        sseCache.remove(id);
    }

    /**
     * 服务器端正常断开连接回调
     * @param
     * @param id
     */
    default  void  sseSuccessCall(String id){
        SseEmitter sseEmitter = sseCache.get(id);
        sseCache.remove(id);
    }

    /**
     * builder  init  SseEmitter  注册 events
     */
    default  SseEmitter.SseEventBuilder builder(String [] events,String id){
        Map<String,Object>  restMap = Maps.newHashMap();
        restMap.put("events",events);
        //状态订阅
        restMap.put("status","subscribed");
        //订阅标识
        restMap.put("id",id);

        SseEmitter.SseEventBuilder builder = SseEmitter
                .event().comment("")//.reconnectTime(2000)
                //订阅
                .name("connect")
                .data(JSON.toJSONString(restMap));
        return  builder;
    }
}
