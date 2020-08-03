package com.jiu.service.common;

import com.jiu.base.service.SuperService;
import com.jiu.entity.common.Parameter;

/**
 * <p>
 * 业务接口
 * 参数配置
 * </p>
 *
 */
public interface ParameterService extends SuperService<Parameter> {
    /**
     * 根据参数键查询参数值
     *
     * @param key    参数键
     * @param defVal 参数值
     * @return
     */
    String getValue(String key, String defVal);
}
