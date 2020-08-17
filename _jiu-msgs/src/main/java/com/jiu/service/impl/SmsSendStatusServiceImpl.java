package com.jiu.service.impl;


import com.jiu.base.service.SuperServiceImpl;
import com.jiu.dao.SmsSendStatusMapper;
import com.jiu.entity.SmsSendStatus;
import com.jiu.service.SmsSendStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 短信发送状态
 * </p>
 *
 */
@Slf4j
@Service

public class SmsSendStatusServiceImpl extends SuperServiceImpl<SmsSendStatusMapper, SmsSendStatus> implements SmsSendStatusService {

}
