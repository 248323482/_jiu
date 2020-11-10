package com.jiu.common.properties;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * 忽略token 配置类
 * <p>
 * 做接口权限时，考虑修改成读取配置文件
 *
 */
@Data
@ConfigurationProperties(prefix = IgnoreTokenProperties.PREFIX)
public class IgnoreTokenProperties {
    private List<String> tenant = CollUtil.newArrayList();
    private List<String> token = CollUtil.newArrayList();
    public static final String PREFIX = "ignore.token";
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    private List<String> baseUri = CollUtil.newArrayList(
            "/**/*.css",
            "/**/*.js",
            "/**/*.html",
            "/**/*.ico",
            "/**/*.jpg",
            "/**/*.jpeg",
            "/**/*.png",
            "/**/*.gif",
            "/**/v2/**",
            "/**/swagger-resources/**",
            "/**/webjars/**",
            "/actuator/**",
            "/error",
            "/**/anno/**",
            "/**/static/**",
            "/**/login"
    );

    public boolean isIgnoreToken(String path) {
        List<String> all = new ArrayList<>();
        all.addAll(getBaseUri());
        all.addAll(getToken());
        return all.stream().anyMatch(url -> path.startsWith(url) || ANT_PATH_MATCHER.match(url, path));
    }
    public boolean isIgnoreTenant(String path) {
        List<String> all = new ArrayList<>();
        all.addAll(getBaseUri());
        all.addAll(getTenant());
        return all.stream().anyMatch(url -> path.startsWith(url) || ANT_PATH_MATCHER.match(url, path));
    }
}
