package com.jiu.cache.redis.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;


@Slf4j
public class Consumer extends Thread{
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;
    @Value("${redis.queue.key:queue}")
    private String queueKey;
    @Value("${redis.queue.pop.timeout:1000}")
    private Long popTimeout;

    private volatile boolean flag = true;
    @Override
    public void run() {
        try {
            while(flag && !Thread.currentThread().isInterrupted()) {
                Object message = redisTemplate.opsForList().rightPop(queueKey, popTimeout, TimeUnit.SECONDS);
                System.out.println("接收到了" + message);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
