package com.jiu.web.controller;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.jiu.base.R;
import com.jiu.base.entity.SuperEntity;
import com.jiu.common.constant.CacheKey;
import com.jiu.dto.VerificationCodeDTO;
import com.jiu.eache.repository.CacheRepository;
import com.jiu.entity.SmsTask;
import com.jiu.entity.enumeration.SourceType;
import com.jiu.entity.enumeration.TemplateCodeType;
import com.jiu.service.SmsTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用验证码
 *
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/verification")
@Api(value = "VerificationCode", tags = "验证码")
public class VerificationCodeController {

    @Autowired
    private CacheRepository cacheRepository;
    @Autowired
    private SmsTaskService smsTaskService;

    /**
     * 通用的发送验证码功能
     * <p>
     * 一个系统可能有很多地方需要发送验证码（注册、找回密码等），每增加一个场景，VerificationCodeType 就需要增加一个枚举值
     *
     * @param data
     * @return
     */
    @ApiOperation(value = "发送验证码", notes = "发送验证码")
    @PostMapping(value = "/send")
    public R<Boolean> send(@Validated @RequestBody VerificationCodeDTO data) {
        String code = RandomUtil.randomNumbers(6);

        SmsTask smsTask = SmsTask.builder().build();
        smsTask.setSourceType(SourceType.SERVICE);
        JSONObject param = new JSONObject();
        param.put("1", code);
        smsTask.setTemplateParams(param.toString());
        smsTask.setReceiver(data.getMobile());
        smsTask.setDraft(false);
        smsTaskService.saveTask(smsTask, TemplateCodeType.COMMON_SMS);

        String key = CacheKey.buildTenantKey(CacheKey.REGISTER_USER, data.getType().name(), data.getMobile());
        cacheRepository.setExpire(key, code, CacheRepository.DEF_TIMEOUT_5M);
        return R.success();
    }

    /**
     * 对某种类型的验证码进行校验
     *
     * @param data
     * @return
     */
    @ApiOperation(value = "验证验证码", notes = "验证验证码")
    @PostMapping
    public R<Boolean> verification(@Validated(SuperEntity.Update.class) @RequestBody VerificationCodeDTO data) {
        String key = CacheKey.buildTenantKey(CacheKey.REGISTER_USER, data.getType().name(), data.getMobile());
        String code = cacheRepository.get(key);
        return R.success(data.getCode().equals(code));
    }
}
