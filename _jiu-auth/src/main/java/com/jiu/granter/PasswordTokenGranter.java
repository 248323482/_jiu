package com.jiu.granter;

import com.jiu.base.R;
import com.jiu.dto.LoginParamDTO;
import com.jiu.jwt.model.AuthInfo;
import org.springframework.stereotype.Component;

/**
 * 账号密码登录获取token
 *
 */
@Component(PasswordTokenGranter.GRANT_TYPE)
public class PasswordTokenGranter extends AbstractTokenGranter implements TokenGranter {

    public static final String GRANT_TYPE = "password";

    @Override
    public R<AuthInfo> grant(LoginParamDTO tokenParameter) {
        return login(tokenParameter);
    }
}
