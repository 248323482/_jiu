package com.jiu.web.controller;

import cn.hutool.core.util.ArrayUtil;
import com.jiu.base.BaseEnum;
import com.jiu.base.R;
import com.jiu.common.enums.HttpMethod;
import com.jiu.database.mybatis.auth.DataScopeType;
import com.jiu.entity.enumeration.*;
import com.jiu.entity.enumeration.ApplicationAppTypeEnum;
import com.jiu.entity.enumeration.AuthorizeType;
import com.jiu.entity.enumeration.DataType;
import com.jiu.entity.enumeration.LogType;
import com.jiu.entity.enumeration.MsgsBizType;
import com.jiu.entity.enumeration.MsgsCenterType;
import com.jiu.entity.enumeration.ProviderType;
import com.jiu.entity.enumeration.SendStatus;
import com.jiu.entity.enumeration.Sex;
import com.jiu.entity.enumeration.SourceType;
import com.jiu.entity.enumeration.TaskStatus;
import com.jiu.entity.enumeration.TenantStatusEnum;
import com.jiu.entity.enumeration.TenantTypeEnum;
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
        ENUM_MAP.put(LogType.class.getSimpleName(), BaseEnum.getMap(LogType.values()));
        ENUM_MAP.put(AuthorizeType.class.getSimpleName(), BaseEnum.getMap(AuthorizeType.values()));
        ENUM_MAP.put(Sex.class.getSimpleName(), BaseEnum.getMap(Sex.values()));
        ENUM_MAP.put(TenantTypeEnum.class.getSimpleName(), BaseEnum.getMap(TenantTypeEnum.values()));
        ENUM_MAP.put(TenantStatusEnum.class.getSimpleName(), BaseEnum.getMap(TenantStatusEnum.values()));
        ENUM_MAP.put(ApplicationAppTypeEnum.class.getSimpleName(), BaseEnum.getMap(ApplicationAppTypeEnum.values()));
        // 文件服务
        ENUM_MAP.put(DataType.class.getSimpleName(), BaseEnum.getMap(HttpMethod.values()));
        //消息服务
        ENUM_MAP.put(MsgsCenterType.class.getSimpleName(), BaseEnum.getMap(MsgsCenterType.values()));
        ENUM_MAP.put(MsgsBizType.class.getSimpleName(), BaseEnum.getMap(MsgsBizType.values()));
        ENUM_MAP.put(ProviderType.class.getSimpleName(), BaseEnum.getMap(ProviderType.values()));
        ENUM_MAP.put(SourceType.class.getSimpleName(), BaseEnum.getMap(SourceType.values()));
        ENUM_MAP.put(SendStatus.class.getSimpleName(), BaseEnum.getMap(SendStatus.values()));
        ENUM_MAP.put(TaskStatus.class.getSimpleName(), BaseEnum.getMap(TaskStatus.values()));
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

