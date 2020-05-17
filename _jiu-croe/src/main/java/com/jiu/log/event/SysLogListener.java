package com.jiu.log.event;


import com.jiu.context.BaseContextHandler;
import com.jiu.log.entity.OptLogDTO;
import com.jiu.log.monitor.PointUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import java.util.function.Consumer;


/**
 * 异步监听日志事件
 *
 */
@Slf4j
@AllArgsConstructor
public class SysLogListener {

    private Consumer<OptLogDTO> consumer;

    @Async
    @Order
    @EventListener(SysLogEvent.class)
    public void saveSysLog(SysLogEvent event) {
        OptLogDTO sysLog = (OptLogDTO) event.getSource();
        BaseContextHandler.setTenant(sysLog.getTenantCode());
        consumer.accept(sysLog);
        PointUtil.info("","",sysLog.toString());
    }

}
