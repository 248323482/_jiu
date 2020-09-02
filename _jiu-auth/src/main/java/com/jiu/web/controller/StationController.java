package com.jiu.web.controller;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiu.base.R;
import com.jiu.base.controller.SuperCacheController;
import com.jiu.base.model.RemoteData;
import com.jiu.base.request.PageParams;
import com.jiu.dto.StationPageDTO;
import com.jiu.dto.croe.StationSaveDTO;
import com.jiu.dto.croe.StationUpdateDTO;
import com.jiu.entity.common.Station;
import com.jiu.security.annotation.PreAuth;
import com.jiu.service.common.StationService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * 岗位
 * </p>
 *
 */
@Slf4j
@RestController
@RequestMapping("/station")
@Api(value = "Station", tags = "岗位")
@PreAuth(replace = "station:")
public class StationController extends SuperCacheController<StationService, Long, Station, StationPageDTO, StationSaveDTO, StationUpdateDTO> {

    @Override
    public void query(PageParams<StationPageDTO> params, IPage<Station> page, Long defSize) {
        baseService.findStationPage(page, params.getModel());
    }

    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<Station> stationList = list.stream().map((map) -> {
            Station item = new Station();
            item.setDescribe(map.getOrDefault("描述", ""));
            item.setName(map.getOrDefault("名称", ""));
            item.setOrg(new RemoteData<>(Convert.toLong(map.getOrDefault("组织", ""))));
            item.setStatus(Convert.toBool(map.getOrDefault("状态", "")));
            return item;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(stationList));
    }

}
