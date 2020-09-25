package com.jiu.web.controller;


import com.jiu.base.controller.QueryController;
import com.jiu.base.controller.SuperSimpleController;
import com.jiu.entity.SmsSendStatus;
import com.jiu.service.SmsSendStatusService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * 短信发送状态
 * </p>
 *
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/smsSendStatus")
@Api(value = "SmsSendStatus", tags = "短信发送状态")
public class SmsSendStatusController extends SuperSimpleController<SmsSendStatusService, SmsSendStatus>
        implements QueryController<SmsSendStatus, Long, SmsSendStatus> {

    public static void main(String[] args) {

    }
}
