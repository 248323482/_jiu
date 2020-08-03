package com.jiu.event.listener;

import cn.hutool.core.util.StrUtil;
import com.jiu.event.LoginEvent;
import com.jiu.event.model.LoginStatusDTO;
import com.jiu.service.UserService;
import com.jiu.service.UserTokenService;
import com.jiu.service.common.LoginLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 登录事件监听，用于记录登录日志
 *
 */
@Component
@Slf4j
public class LoginListener {
    @Autowired
    private LoginLogService loginLogService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserTokenService userTokenService;

    @Async
    @EventListener({LoginEvent.class})
    public void saveSysLog(LoginEvent event) {
        LoginStatusDTO loginStatus = (LoginStatusDTO) event.getSource();

        if (StrUtil.isEmpty(loginStatus.getTenant())) {
            log.warn("忽略记录登录日志:{}", loginStatus);
            return;
        }

        if (LoginStatusDTO.Type.SUCCESS == loginStatus.getType()) {
            // 重置错误次数 和 最后登录时间
            this.userService.resetPassErrorNum(loginStatus.getId());

            userTokenService.save(loginStatus.getUserToken());
        } else if (LoginStatusDTO.Type.PWD_ERROR == loginStatus.getType()) {
            // 密码错误
            this.userService.incrPasswordErrorNumById(loginStatus.getId());
        }
        loginLogService.save(loginStatus.getId(), loginStatus.getAccount(), loginStatus.getUa(), loginStatus.getIp(), loginStatus.getLocation(), loginStatus.getDescription());
    }
}
