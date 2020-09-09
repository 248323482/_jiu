package com.jiu.web.controller;


import com.jiu.base.R;
import com.jiu.base.controller.SuperController;
import com.jiu.database.mybatis.auth.DataScopeType;
import com.jiu.database.mybatis.conditions.Wraps;
import com.jiu.dto.SmsTemplateSaveDTO;
import com.jiu.dto.SmsTemplateUpdateDTO;
import com.jiu.entity.SmsTemplate;
import com.jiu.security.annotation.PreAuth;
import com.jiu.service.SmsTemplateService;
import com.jiu.utils.BeanPlusUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * 短信模板
 * </p>
 *
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/smsTemplate")
@Api(value = "SmsTemplate", tags = "短信模板")
@PreAuth(replace = "sms:template:")
public class SmsTemplateController extends SuperController<SmsTemplateService, Long, SmsTemplate, SmsTemplate, SmsTemplateSaveDTO, SmsTemplateUpdateDTO> {

    @Override
    public R<SmsTemplate> handlerSave(SmsTemplateSaveDTO data) {
        SmsTemplate smsTemplate = BeanPlusUtil.toBean(data, SmsTemplate.class);
        baseService.saveTemplate(smsTemplate);
        return success(smsTemplate);
    }

    @Override
    public R<SmsTemplate> handlerUpdate(SmsTemplateUpdateDTO model) {
        SmsTemplate smsTemplate = BeanPlusUtil.toBean(model, SmsTemplate.class);
        baseService.updateTemplate(smsTemplate);
        return success(smsTemplate);
    }

    @ApiOperation(value = "检测自定义编码是否存在", notes = "检测自定义编码是否存在")
    @GetMapping("/check")
    @PreAuth("hasPermit('{}view')")
    public R<Boolean> check(@RequestParam(value = "customCode") String customCode) {
        int count = baseService.count(Wraps.<SmsTemplate>lbQ().eq(SmsTemplate::getCustomCode, customCode));
        return success(count > 0);
    }
    @RequestMapping("test")
    public void t(@RequestBody DataScopeType dataScopeType ){

    }


}
