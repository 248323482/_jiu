package com.jiu.service.common;

import com.jiu.base.service.SuperCacheService;
import com.jiu.entity.common.Area;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 地区表
 * </p>
 *
 */
public interface AreaService extends SuperCacheService<Area> {

    /**
     * 递归删除
     *
     * @param ids
     * @return
     */
    boolean recursively(List<Long> ids);
}
