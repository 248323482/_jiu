package com.jiu.web.controller;


import cn.hutool.core.util.RandomUtil;
import com.jiu.base.R;
import com.jiu.base.controller.SuperController;
import com.jiu.dto.ApplicationPageDTO;
import com.jiu.dto.ApplicationSaveDTO;
import com.jiu.dto.ApplicationUpdateDTO;
import com.jiu.entity.Application;
import com.jiu.security.annotation.PreAuth;
import com.jiu.service.ApplicationService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * 应用
 * </p>
 *
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/application")
@Api(value = "Application", tags = "应用")
@PreAuth(replace = "application:")
public class ApplicationController extends SuperController<ApplicationService, Long, Application, ApplicationPageDTO, ApplicationSaveDTO, ApplicationUpdateDTO> {

    @Override
    public R<Application> handlerSave(ApplicationSaveDTO applicationSaveDTO) {
        applicationSaveDTO.setClientId(RandomUtil.randomString(24));
        applicationSaveDTO.setClientSecret(RandomUtil.randomString(32));
        return super.handlerSave(applicationSaveDTO);
    }

}
