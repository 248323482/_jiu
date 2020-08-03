package com.jiu.granter;



import com.jiu.base.R;
import com.jiu.dto.LoginParamDTO;
import com.jiu.jwt.model.AuthInfo;

/**
 * 授权认证统一接口.
 *

 */
public interface TokenGranter {

    /**
     * 获取用户信息
     *
     * @param loginParam 授权参数
     * @return LoginDTO
     */
    R<AuthInfo> grant(LoginParamDTO loginParam);

}
