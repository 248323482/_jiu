package com.jiu.j2cache;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * j2cache配置项
 **/
@ConfigurationProperties("jiu.j2cache")
@Data
public class J2cacheProperties {
    private boolean enable = true;
    private String broadcast = "lettuce";
    private L1cacheDefinition l1cache = new L1cacheDefinition();
    private L2cacheDefinition l2cache = new L2cacheDefinition();
    //json
    private String serialization = "fst";
    private boolean syncTtlToRedis = true;
    private boolean cacheNullObject = true;
    private List<CaffeineDefinition> caffeine = new ArrayList<>();

    /**
     * 二级缓存定义
     **/
    @Data
    class L2cacheDefinition {
        private String name = "lettuce";
    }

    @Data
    class L1cacheDefinition {
        private String name = "caffeine";
    }

    @Data
    public class CaffeineDefinition {
        private String key;
        private String value;
    }


}

