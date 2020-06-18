package com.jiu.api;

import com.jiu.api.hystrix.StationApiFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 岗位API
 *
 */
@FeignClient(name = "jiu-common-server", path = "/station",
        qualifier = "stationApi", fallback = StationApiFallback.class)
public interface StationApi {

    /**
     * 根据id查询 岗位
     *
     * @param ids
     * @return
     */
    @GetMapping("/findStationByIds")
    Map<Serializable, Object> findStationByIds(@RequestParam(value = "ids") Set<Serializable> ids);

    /**
     * 根据id查询 岗位名称
     *
     * @param ids
     * @return
     */
    @GetMapping("/findStationNameByIds")
    Map<Serializable, Object> findStationNameByIds(@RequestParam(value = "ids") Set<Serializable> ids);
}
