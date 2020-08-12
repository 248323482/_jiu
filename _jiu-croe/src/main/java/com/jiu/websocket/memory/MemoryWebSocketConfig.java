package com.jiu.websocket.memory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.jiu.websocket.WebSocketManager;
import com.jiu.websocket.config.WebSocketHeartBeatChecker;
import com.jiu.websocket.utils.SpringContextHolder;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author xiongshiyan
 * 内存管理websocket配置
 */
public class MemoryWebSocketConfig {
    /**
     * applicationContext全局保存器
     */
    @Bean
    @ConditionalOnMissingBean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }

    @Bean(WebSocketManager.WEBSOCKET_MANAGER_NAME)
    @ConditionalOnMissingBean(name = WebSocketManager.WEBSOCKET_MANAGER_NAME)
    public WebSocketManager webSocketManager() {
        return new MemWebSocketManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    @ConditionalOnMissingBean
    public WebSocketHeartBeatChecker webSocketHeartBeatChecker() {
        return new WebSocketHeartBeatChecker();
    }
}