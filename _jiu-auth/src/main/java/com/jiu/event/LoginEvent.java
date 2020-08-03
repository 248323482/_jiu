package com.jiu.event;

import com.jiu.event.model.LoginStatusDTO;
import org.springframework.context.ApplicationEvent;

/**
 * 登录事件
 *
 */
public class LoginEvent extends ApplicationEvent {
    public LoginEvent(LoginStatusDTO source) {
        super(source);
    }
}
