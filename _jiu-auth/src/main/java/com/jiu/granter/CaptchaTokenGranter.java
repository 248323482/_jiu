package com.jiu.granter;

import com.jiu.dto.LoginParamDTO;
import com.jiu.event.LoginEvent;
import com.jiu.event.model.LoginStatusDTO;
import com.jiu.base.R;
import com.jiu.context.BaseContextHandler;
import com.jiu.exception.BizException;
import com.jiu.jwt.model.AuthInfo;
import com.jiu.service.ValidateCodeService;
import com.jiu.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 验证码TokenGranter
 *
 * @author Chill
 */
@Component(CaptchaTokenGranter.GRANT_TYPE)
@Slf4j
public class CaptchaTokenGranter extends AbstractTokenGranter implements TokenGranter {

    public static final String GRANT_TYPE = "captcha";
    @Autowired
    private ValidateCodeService validateCodeService;

    @Override
    public R<AuthInfo> grant(LoginParamDTO loginParam) {
        R<Boolean> check = validateCodeService.check(loginParam.getKey(), loginParam.getCode());
        if (check.getIsError()) {
            String msg = check.getMsg();
            BaseContextHandler.setTenant(loginParam.getTenant());
            SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.fail(loginParam.getAccount(), msg)));
            throw BizException.validFail(check.getMsg());
        }

        return login(loginParam);
    }

}
