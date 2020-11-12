package com.jiu.web.controller;

import cn.hutool.core.util.IdUtil;
import com.jiu.base.R;
import com.jiu.database.properties.DatabaseProperties;
import com.jiu.security.annotation.LoginUser;
import com.jiu.security.model.SysUser;
import com.jiu.service.common.LoginLogService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * 首页
 * </p>
 *
 */
@Slf4j
@Validated
@RestController
@Api(value = "dashboard", tags = "首页")
public class DashboardController {

    @Autowired
    private LoginLogService loginLogService;
    @Autowired
    private DatabaseProperties databaseProperties;

    /**
     * 最近10天访问记录
     *
     * @return
     */
    @GetMapping("/dashboard/visit")
    public R<Map<String, Object>> index(@ApiIgnore @LoginUser SysUser user) {
        Map<String, Object> data = new HashMap<>();
        // 获取系统访问记录
        data.put("totalVisitCount", loginLogService.findTotalVisitCount());
        data.put("todayVisitCount", loginLogService.findTodayVisitCount());
        data.put("todayIp", loginLogService.findTodayIp());

        data.put("lastTenVisitCount", loginLogService.findLastTenDaysVisitCount(null));
        data.put("lastTenUserVisitCount", loginLogService.findLastTenDaysVisitCount(user.getAccount()));

        data.put("browserCount", loginLogService.findByBrowser());
        data.put("operatingSystemCount", loginLogService.findByOperatingSystem());
        return R.success(data);
    }

    /**
     * 生成id
     *
     * @return
     */
    @GetMapping("/common/generateId")
    public R<Long> generate() {
        DatabaseProperties.HutoolId id = databaseProperties.getHutoolId();
        return R.success(IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId()).nextId());
    }
}