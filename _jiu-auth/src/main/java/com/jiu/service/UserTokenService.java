package com.jiu.service;

import com.jiu.base.service.SuperService;
import com.jiu.entity.UserToken;

/**
 */
public interface UserTokenService extends SuperService<UserToken> {
    /**
     * 重置用户登录
     *
     * @return
     */
    boolean reset();
}
