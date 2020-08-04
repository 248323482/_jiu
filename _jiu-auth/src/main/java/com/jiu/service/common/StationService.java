package com.jiu.service.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiu.base.service.SuperCacheService;
import com.jiu.dto.StationPageDTO;
import com.jiu.entity.common.Station;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 业务接口
 * 岗位
 * </p>
 *
 */
public interface StationService extends SuperCacheService<Station> {
    /**
     * 按权限查询岗位的分页信息
     *
     * @param page
     * @param data
     * @return
     */
    IPage<Station> findStationPage(IPage page, StationPageDTO data);

    /**
     * 根据id 查询
     *
     * @param ids
     * @return
     */
    Map<Serializable, Object> findStationByIds(Set<Serializable> ids);

    /**
     * 根据id 查询 岗位名称
     *
     * @param ids
     * @return
     */
    Map<Serializable, Object> findStationNameByIds(Set<Serializable> ids);
}
