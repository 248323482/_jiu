package com.jiu.service.common.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.jiu.dao.common.ParameterMapper;
import com.jiu.entity.common.Parameter;
import com.jiu.event.ParameterUpdateEvent;
import com.jiu.event.model.ParameterUpdate;
import com.jiu.service.common.ParameterService;
import com.jiu.base.service.SuperServiceImpl;
import com.jiu.context.BaseContextHandler;
import com.jiu.database.mybatis.conditions.Wraps;
import com.jiu.utils.SpringUtils;
import com.jiu.base.service.SuperServiceImpl;
import com.jiu.dao.common.ParameterMapper;
import com.jiu.entity.common.Parameter;
import com.jiu.service.common.ParameterService;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static com.jiu.common.constant.CacheKey.PARAMETER;
import static com.jiu.common.constant.CacheKey.buildTenantKey;

/**
 * <p>
 * 业务实现类
 * 参数配置
 * </p>
 *
 */
@Slf4j
@Service

public class ParameterServiceImpl extends SuperServiceImpl<ParameterMapper, Parameter> implements ParameterService {

    @Autowired
    private CacheChannel channel;

    protected String getRegion() {
        return PARAMETER;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Parameter model) {
        boolean bool = SqlHelper.retBool(baseMapper.insert(model));
        if (bool) {
            String cacheKey = buildTenantKey(model.getKey());
            channel.set(getRegion(), cacheKey, model.getValue());
        }
        return bool;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(Parameter model) {
        boolean bool = SqlHelper.retBool(getBaseMapper().updateById(model));
        if (bool) {
            String cacheKey = buildTenantKey(model.getKey());
            channel.set(getRegion(), cacheKey, model.getValue());

            SpringUtils.publishEvent(new ParameterUpdateEvent(
                    new ParameterUpdate(model.getKey(), model.getValue(), null, BaseContextHandler.getTenant())
            ));
        }
        return bool;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return true;
        }
        List<Parameter> parameterList = super.listByIds(idList);
        if (parameterList.isEmpty()) {
            return true;
        }
        boolean bool = SqlHelper.retBool(getBaseMapper().deleteBatchIds(idList));
        if (bool) {
            String[] cacheKeys = parameterList.stream().map((item) -> buildTenantKey(item.getKey())).toArray(String[]::new);
            channel.evict(getRegion(), cacheKeys);

            parameterList.forEach((model -> {
                SpringUtils.publishEvent(new ParameterUpdateEvent(
                        new ParameterUpdate(model.getKey(), model.getValue(), null, BaseContextHandler.getTenant())
                ));
            }));
        }
        return bool;
    }

    @Override
    public String getValue(String key, String defVal) {
        if (StrUtil.isEmpty(key)) {
            return defVal;
        }

        String cacheKey = buildTenantKey(key);
        CacheObject cacheObject = channel.get(getRegion(), cacheKey, (k) -> {
            Parameter parameter = getOne(Wraps.<Parameter>lbQ().eq(Parameter::getKey, key).eq(Parameter::getStatus, true));
            return parameter == null ? null : parameter.getValue();
        }, true);

        return (String) cacheObject.getValue();
    }
}
