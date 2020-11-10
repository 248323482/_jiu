package com.jiu.xss.properties;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 忽略XSS 配置类
 *
 */
@Data
@ConfigurationProperties(prefix = XssProperties.PREFIX)
public class XssProperties {
    public static final String PREFIX = "jiu.xss";
    private Boolean enabled = true;
    private int order = 1;
    private List<String> patterns = CollUtil.newArrayList("/*");
    private List<String> ignorePaths = CollUtil.newArrayList("favicon.ico",
            "/**/doc.html",
            "/**/swagger-ui.html",
            "/csrf",
            "/webjars/**",
            "/v2/**",
            "/swagger-resources/**",
            "/resources/**",
            "/static/**",
            "/public/**",
            "/classpath:*",
            "/actuator/**",
            "/**/noxss/**",
            "/**/activiti/**",
            "/**/service/model/**",
            "/**/service/editor/**"
    );
    private List<String> ignoreParamValues = CollUtil.newArrayList("noxss");


}
