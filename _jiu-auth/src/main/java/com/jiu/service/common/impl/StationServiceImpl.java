package com.jiu.service.common.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.ImmutableMap;
import com.jiu.base.service.SuperCacheServiceImpl;
import com.jiu.dao.StationMapper;
import com.jiu.database.mybatis.auth.DataScope;
import com.jiu.database.mybatis.conditions.Wraps;
import com.jiu.database.mybatis.conditions.query.LbqWrapper;
import com.jiu.dto.StationPageDTO;
import com.jiu.entity.common.Station;
import com.jiu.injection.annonation.InjectionResult;
import com.jiu.service.common.StationService;
import com.jiu.utils.MapHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.jiu.common.constant.CacheKey.STATION;

/**
 * <p>
 * 业务实现类
 * 岗位
 * </p>
 *
 */
@Slf4j
@Service

public class StationServiceImpl extends SuperCacheServiceImpl<StationMapper, Station> implements StationService {
    @Override
    protected String getRegion() {
        return STATION;
    }

    @Override
    // 启用属性自动注入
    @InjectionResult
    public IPage<Station> findStationPage(IPage page, StationPageDTO data) {
        Station station = BeanUtil.toBean(data, Station.class);

        //Wraps.lbQ(station); 这种写法值 不能和  ${ew.customSqlSegment} 一起使用
        LbqWrapper<Station> wrapper = Wraps.lbQ();

        // ${ew.customSqlSegment} 语法一定要手动eq like 等
        wrapper.like(Station::getName, station.getName())
                .like(Station::getDescribe, station.getDescribe())
                .eq(Station::getOrg, station.getOrg())
                .eq(Station::getStatus, station.getStatus())
                .geHeader(Station::getCreateTime, data.getStartCreateTime())
                .leFooter(Station::getCreateTime, data.getEndCreateTime());
        return baseMapper.findStationPage(page, wrapper, new DataScope());
    }

    @Override
    public Map<Serializable, Object> findStationByIds(Set<Serializable> ids) {
        List<Station> list = getStations(ids);

        //key 是 组织id， value 是org 对象
        ImmutableMap<Serializable, Object> typeMap = MapHelper.uniqueIndex(list, Station::getId, (station) -> station);
        return typeMap;
    }

    @Override
    public Map<Serializable, Object> findStationNameByIds(Set<Serializable> ids) {
        List<Station> list = getStations(ids);
        //key 是 组织id， value 是org 对象
        ImmutableMap<Serializable, Object> typeMap = MapHelper.uniqueIndex(list, Station::getId, Station::getName);
        return typeMap;
    }

    private List<Station> getStations(Set<Serializable> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> idList = ids.stream().mapToLong(Convert::toLong).boxed().collect(Collectors.toList());

        List<Station> list = null;
        if (idList.size() <= 1000) {
            list = idList.stream().map(this::getByIdCache).filter(Objects::nonNull).collect(Collectors.toList());
        } else {
            LbqWrapper<Station> query = Wraps.<Station>lbQ()
                    .in(Station::getId, idList)
                    .eq(Station::getStatus, true);
            list = super.list(query);

            if (!list.isEmpty()) {
                list.forEach(item -> {
                    String itemKey = key(item.getId());
                    cacheChannel.set(getRegion(), itemKey, item);
                });
            }
        }
        return list;
    }

}
