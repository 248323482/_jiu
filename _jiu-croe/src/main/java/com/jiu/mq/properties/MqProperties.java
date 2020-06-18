package com.jiu.mq.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = MqProperties.PREFIX)
public class MqProperties {
    public static final String PREFIX = "jiu.rabbitmq";

    /**
     * 是否启用
     */
    private Boolean enabled = false;

}
