package com.jiu.base.controller;

import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
public interface SseController <Entity, Id extends Serializable, PageDTO> extends PageController<Entity, PageDTO> {
    static Map<String, SseEmitter> sseCache = new ConcurrentHashMap<>();
    @GetMapping(path = "subscribe")
    default SseEmitter push(String id) throws  Exception{
        // 超时时间设置为1小时
        SseEmitter sseEmitter = new SseEmitter(3600_000L);
        sseCache.put(id, sseEmitter);
        //超时
        sseEmitter.onTimeout(() -> sseTimeOutCall(id) );
        //初始化数据组装
        sseEmitter.send(builder());
        sseEmitter.onCompletion(() -> sseSuccessCall(id));
        //心跳线程
        heartbeat(sseEmitter,id);

        return sseEmitter;
    }

    @GetMapping( "push")
    default void push(String id, String content) throws IOException {
        SseEmitter sseEmitter = sseCache.get(id);
        if (sseEmitter != null) {
            sseEmitter.send(content);
        }
    }

    @GetMapping("success")
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
                            .name("heartbeat")
                            .data(new Date().getTime());
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
        sseEmitter=null;
        System.gc();
    }

    /**
     * 服务器端正常断开连接回调
     * @param
     * @param id
     */
    default  void  sseSuccessCall(String id){
        SseEmitter sseEmitter = sseCache.get(id);
        sseCache.remove(id);
        sseEmitter=null;
        System.gc();
    }

    /**
     * builder  init  SseEmitter
     */
    default  SseEmitter.SseEventBuilder builder(){
        SseEmitter.SseEventBuilder builder = SseEmitter
                .event()
                .name("init")
                .data("初始化数据中订单数据。。。。。。");
        return  builder;
    }
}
