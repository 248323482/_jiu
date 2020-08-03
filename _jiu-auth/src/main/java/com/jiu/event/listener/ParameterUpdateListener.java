package com.jiu.event.listener;

import com.jiu.event.ParameterUpdateEvent;
import com.jiu.event.model.ParameterUpdate;
import com.jiu.common.constant.ParameterKey;
import com.jiu.context.BaseContextHandler;
import com.jiu.service.UserTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 参数修改事件监听，用于调整具体的业务
 *
 */
@Component
@Slf4j
public class ParameterUpdateListener {

    @Autowired
    private UserTokenService userTokenService;

    @Async
    @EventListener({ParameterUpdateEvent.class})
    public void saveSysLog(ParameterUpdateEvent event) {
        ParameterUpdate source = (ParameterUpdate) event.getSource();

        BaseContextHandler.setTenant(source.getTenant());
        if (ParameterKey.LOGIN_POLICY.equals(source.getKey())) {
            userTokenService.reset();
        }
    }
}
