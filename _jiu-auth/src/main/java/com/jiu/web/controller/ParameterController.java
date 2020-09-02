package com.jiu.web.controller;


import com.jiu.base.controller.SuperController;
import com.jiu.dto.ParameterPageDTO;
import com.jiu.dto.ParameterSaveDTO;
import com.jiu.dto.ParameterUpdateDTO;
import com.jiu.entity.common.Parameter;
import com.jiu.security.annotation.PreAuth;
import com.jiu.service.common.ParameterService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * 参数配置
 * </p>
 *
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/parameter")
@Api(value = "Parameter", tags = "参数配置")
@PreAuth(replace = "parameter:")
public class ParameterController extends SuperController<ParameterService, Long, Parameter, ParameterPageDTO, ParameterSaveDTO, ParameterUpdateDTO> {

}
