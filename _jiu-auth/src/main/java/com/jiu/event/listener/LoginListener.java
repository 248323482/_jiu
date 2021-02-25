package com.jiu.event.listener;

import cn.hutool.core.util.StrUtil;
import com.jiu.context.BaseContextHandler;
import com.jiu.database.properties.DatabaseProperties;
import com.jiu.database.properties.MultiTenantType;
import com.jiu.event.LoginEvent;
import com.jiu.event.model.LoginStatusDTO;
import com.jiu.service.UserService;
import com.jiu.service.UserTokenService;
import com.jiu.service.common.LoginLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 登录事件监听，用于记录登录日志
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LoginListener {
    private final LoginLogService loginLogService;
    private final UserService userService;
    private final DatabaseProperties databaseProperties;

    @Async
    @EventListener({LoginEvent.class})
    public void saveSysLog(LoginEvent event) {
        LoginStatusDTO loginStatus = (LoginStatusDTO) event.getSource();

        if (!MultiTenantType.NONE.eq(databaseProperties.getMultiTenantType()) && StrUtil.isEmpty(loginStatus.getTenant())) {
            log.warn("忽略记录登录日志:{}", loginStatus);
            return;
        }

        BaseContextHandler.setTenant(loginStatus.getTenant());
        if (LoginStatusDTO.Type.SUCCESS == loginStatus.getType()) {
            // 重置错误次数 和 最后登录时间
            this.userService.resetPassErrorNum(loginStatus.getId());


        } else if (LoginStatusDTO.Type.PWD_ERROR == loginStatus.getType()) {
            // 密码错误
            this.userService.incrPasswordErrorNumById(loginStatus.getId());
        }
        loginLogService.save(loginStatus.getId(), loginStatus.getAccount(), loginStatus.getUa(), loginStatus.getIp(), loginStatus.getLocation(), loginStatus.getDescription());
    }

}
