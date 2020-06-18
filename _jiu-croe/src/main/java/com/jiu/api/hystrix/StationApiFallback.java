package com.jiu.api.hystrix;

import com.jiu.api.StationApi;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 熔断类
 */
@Component
public class StationApiFallback implements StationApi {
    @Override
    public Map<Serializable, Object> findStationByIds(Set<Serializable> ids) {
        return Collections.emptyMap();
    }

    @Override
    public Map<Serializable, Object> findStationNameByIds(Set<Serializable> ids) {
        return Collections.emptyMap();
    }
}
