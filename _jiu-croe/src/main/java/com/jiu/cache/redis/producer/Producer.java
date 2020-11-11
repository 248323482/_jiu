package com.jiu.cache.redis.producer;

import com.alibaba.fastjson.JSON;
import com.jiu.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

/**
 * @Author Administrator
 * @create 2020/8/14 14:34
 */
@Slf4j
public class Producer {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${redis.queue.key:queue}")
    private String queueKey;

    public Long sendMeassage(Object object) {
        System.out.println("发送了" + object);
        return redisTemplate.opsForList().leftPush(queueKey, object);
    }
}
