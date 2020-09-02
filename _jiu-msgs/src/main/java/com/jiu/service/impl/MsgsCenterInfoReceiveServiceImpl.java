package com.jiu.service.impl;


import com.jiu.base.service.SuperServiceImpl;
import com.jiu.dao.MsgsCenterInfoReceiveMapper;
import com.jiu.entity.MsgsCenterInfoReceive;
import com.jiu.service.MsgsCenterInfoReceiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 消息中心 接收表
 * 全量数据
 * </p>
 *
 */
@Slf4j
@Service

public class MsgsCenterInfoReceiveServiceImpl extends SuperServiceImpl<MsgsCenterInfoReceiveMapper, MsgsCenterInfoReceive> implements MsgsCenterInfoReceiveService {

}
