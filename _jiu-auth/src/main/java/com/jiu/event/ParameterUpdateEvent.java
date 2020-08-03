package com.jiu.event;

import com.jiu.event.model.ParameterUpdate;
import org.springframework.context.ApplicationEvent;

/**
 * 登录事件
 *
 */
public class ParameterUpdateEvent extends ApplicationEvent {
    public ParameterUpdateEvent(ParameterUpdate source) {
        super(source);
    }
}
