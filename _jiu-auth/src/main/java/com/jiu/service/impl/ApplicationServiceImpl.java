package com.jiu.service.impl;


import com.jiu.base.service.SuperServiceImpl;
import com.jiu.dao.ApplicationMapper;
import com.jiu.entity.Application;
import com.jiu.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 应用
 * </p>
 *
 */
@Slf4j
@Service
public class ApplicationServiceImpl extends SuperServiceImpl<ApplicationMapper, Application> implements ApplicationService {

}
