package com.jiu.web.controller;

import cn.hutool.core.util.ArrayUtil;
import com.jiu.base.BaseEnum;
import com.jiu.base.R;
import com.jiu.common.enums.HttpMethod;
import com.jiu.database.mybatis.auth.DataScopeType;
import com.jiu.entity.enumeration.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用 控制器
 *
 */
@Slf4j
@RestController
@RefreshScope
@Api(value = "Common", tags = "通用Controller")
public class OauthGeneralController {

    private final static Map<String, Map<String, String>> ENUM_MAP = new HashMap<>(8);

    static {
        // 权限服务
        ENUM_MAP.put(HttpMethod.class.getSimpleName(), BaseEnum.getMap(HttpMethod.values()));
        ENUM_MAP.put(DataScopeType.class.getSimpleName(), BaseEnum.getMap(DataScopeType.values()));
        //ENUM_MAP.put(MsgsCenterType.class.getSimpleName(), BaseEnum.getMap(MsgsCenterType.values()));
       // ENUM_MAP.put(MsgsBizType.class.getSimpleName(), BaseEnum.getMap(MsgsBizType.values()));
    }


    @ApiOperation(value = "获取当前系统指定枚举", notes = "获取当前系统指定枚举")
    @GetMapping("/enums")
    public R<Map<String, Map<String, String>>> enums(@RequestParam(value = "codes[]", required = false) String[] codes) {
        if (ArrayUtil.isEmpty(codes)) {
            return R.success(ENUM_MAP);
        }

        Map<String, Map<String, String>> map = new HashMap<>(codes.length);

        for (String code : codes) {
            if (ENUM_MAP.containsKey(code)) {
                map.put(code, ENUM_MAP.get(code));
            }
        }
        return R.success(map);
    }



}

