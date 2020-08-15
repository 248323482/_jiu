package com.jiu.mq.consumer;

import com.jiu.mq.constant.QueueConstants;
import com.jiu.mq.constant.QueueConstants.ExchangeNames;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Administrator
 * @create 2020/8/14 14:48
 */

@Slf4j
//@Configuration
public class Consumer {
    @SneakyThrows
    @RabbitHandler
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = QueueConstants.QueueNames.dataSourceQueue), exchange = @Exchange(value = ExchangeNames.dataSourceExchange, type = ExchangeTypes.FANOUT)))
    public void recieved(Message message, Channel channel) {
        channel.basicQos(0,1,false);
        MessageProperties properties = message.getMessageProperties();

        long tag = properties.getDeliveryTag();
        if (true) {
            //逻辑执行成功更新队列状态
            channel.basicAck(tag, false);// 消费确认
        } else {
            //消费失败不做操作
            channel.basicNack(tag, false, false);
        }
    }
}
