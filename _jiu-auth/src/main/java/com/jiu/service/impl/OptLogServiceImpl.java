package com.jiu.service.impl;


import com.jiu.base.service.SuperServiceImpl;
import com.jiu.dao.OptLogMapper;
import com.jiu.entity.OptLog;
import com.jiu.log.entity.OptLogDTO;
import com.jiu.service.OptLogService;
import com.jiu.utils.BeanPlusUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 业务实现类
 * 系统日志
 * </p>
 *
 */
@Slf4j
@Service

public class OptLogServiceImpl extends SuperServiceImpl<OptLogMapper, OptLog> implements OptLogService {

    @Override
    public boolean save(OptLogDTO entity) {
        return super.save(BeanPlusUtil.toBean(entity, OptLog.class));
    }

    @Override
    public boolean clearLog(LocalDateTime clearBeforeTime, Integer clearBeforeNum) {
        return baseMapper.clearLog(clearBeforeTime, clearBeforeNum);
    }
}
