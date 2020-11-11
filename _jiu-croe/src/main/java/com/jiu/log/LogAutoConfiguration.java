package com.jiu.log;


import com.jiu.log.aspect.SysLogAspect;
import com.jiu.log.event.SysLogListener;
import com.jiu.log.monitor.PointUtil;
import com.jiu.log.properties.OptLogProperties;
import com.plumelog.core.TraceId;
import com.plumelog.core.util.IdWorker;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 日志自动配置
 * <p>
 * 启动条件：
 * 1，存在web环境
 */
@EnableAsync
@Configuration
@AllArgsConstructor
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = OptLogProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(OptLogProperties.class)
public class LogAutoConfiguration {

    // IdWorker
    @Component
    public class Interceptor extends HandlerInterceptorAdapter {
        private IdWorker worker = new IdWorker(1,1,1);//雪花算法，这边不一定要用这个生成id
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            TraceId.logTraceID.set(String.valueOf(worker.nextId()));//设置TraceID值，不埋此点链路ID就没有
            return true;
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public SysLogAspect sysLogAspect() {
        return new SysLogAspect();
    }


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression("${jiu.log.enabled:true} && 'LOGGER'.equals('${jiu.log.type:LOGGER}')")
    public SysLogListener sysLogListener() {
        return new SysLogListener((log) -> {
            PointUtil.debug("0", "OPT_LOG", "");
        });
    }
}
