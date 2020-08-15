package com.jiu.mq.producer;

import com.alibaba.fastjson.JSON;
import com.jiu.utils.StrPool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 * @Author Administrator
 * @create 2020/8/14 14:34
 */
@Slf4j
//@Configuration
public class Producer {
    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    public void send(String exchangeName, String queueName, Object msg) {
        CorrelationData correlationData = new CorrelationData(queueName+ StrPool.UNDERSCORE+new Date().getTime());
        log.info("send mq msg begin!...exchangeName:{},queueName:{},msg:{}", exchangeName, queueName, JSON.toJSONString(msg));
        // 第一个参数为刚刚定义的队列名称
        rabbitTemplate.convertAndSend(exchangeName, queueName, msg, correlationData);
        log.info("send mq msg end!...queueName:{}", queueName);
    }
}
