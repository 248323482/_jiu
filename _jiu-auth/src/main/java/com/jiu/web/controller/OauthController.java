package com.jiu.web.controller;

import cn.hutool.core.util.StrUtil;
import com.jiu.base.R;
import com.jiu.base.response.IgnoreResponseBodyAdvice;
import com.jiu.context.BaseContextHandler;
import com.jiu.dto.LoginParamDTO;
import com.jiu.exception.BizException;
import com.jiu.jwt.TokenUtil;
import com.jiu.jwt.model.AuthInfo;
import com.jiu.jwt.utils.JwtUtil;
import com.jiu.granter.TokenGranter;
import com.jiu.granter.TokenGranterBuilder;
import com.jiu.service.AdminService;
import com.jiu.service.ValidateCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证Controller
 *
 */
@Slf4j
@RestController
@RequestMapping("/anno")
@AllArgsConstructor
@Api(value = "用户授权认证", tags = "登录接口")
public class OauthController {

    @Autowired
    private ValidateCodeService validateCodeService;
    @Autowired
    private TokenGranterBuilder tokenGranterBuilder;
    @Autowired
    private TokenUtil tokenUtil;
    @Autowired
    private AdminService authManager;

    /**
     *
     * @param login
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "获取认证token", notes = "登录或者清空缓存时调用")
    @PostMapping(value = "/token")
    public R<AuthInfo> login(@Validated @RequestBody LoginParamDTO login) throws BizException {
        if (StrUtil.isEmpty(login.getTenant())) {
            login.setTenant(BaseContextHandler.getTenant());
        }
        login.setTenant(JwtUtil.base64Decoder(login.getTenant()));

        TokenGranter granter = tokenGranterBuilder.getGranter(login.getGrantType());
        R<AuthInfo> userInfo = granter.grant(login);

        return userInfo;
    }
    /**
     * 验证验证码
     *
     * @param key  验证码唯一uuid key
     * @param code 验证码
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "验证验证码", notes = "验证验证码")
    @GetMapping(value = "/check")
    public R<Boolean> check(@RequestParam(value = "key") String key, @RequestParam(value = "code") String code) throws BizException {
        return this.validateCodeService.check(key, code);
    }

    @ApiOperation(value = "验证码", notes = "验证码")
    @GetMapping(value = "/captcha", produces = "image/png")
    @IgnoreResponseBodyAdvice
    public void captcha(@RequestParam(value = "key") String key, HttpServletResponse response) throws IOException {
        this.validateCodeService.create(key, response);
    }

    /**
     * 验证token
     *
     * @param token
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "验证token", notes = "验证token")
    @GetMapping(value = "/verify")
    public R<AuthInfo> verify(@RequestParam(value = "token") String token) throws BizException {
        return R.success(tokenUtil.getAuthInfo(token));
    }


    /**
     * 管理员登录 zuihou-admin-ui 系统
     *
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "超级管理员登录", notes = "超级管理员登录")
    @PostMapping(value = "/admin/login")
    public R<AuthInfo> loginAdminTx(@Validated @RequestBody LoginParamDTO login) throws BizException {
        log.info("account={}", login.getAccount());
        R<Boolean> check = this.validateCodeService.check(login.getKey(), login.getCode());
        if (check.getIsError()) {
            return R.fail(check.getMsg());
        }
        return authManager.adminLogin(login.getAccount(), login.getPassword());
    }
}
