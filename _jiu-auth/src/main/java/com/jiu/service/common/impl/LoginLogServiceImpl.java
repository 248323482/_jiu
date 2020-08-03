package com.jiu.service.common.impl;

import cn.hutool.core.util.StrUtil;
import com.jiu.base.service.SuperServiceImpl;
import com.jiu.common.constant.CacheKey;
import com.jiu.dao.common.LoginLogMapper;
import com.jiu.entity.User;
import com.jiu.entity.common.LoginLog;
import com.jiu.service.UserService;
import com.jiu.service.common.LoginLogService;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务实现类
 * 系统日志
 * </p>
 *
 */
@Slf4j
@Service

public class LoginLogServiceImpl extends SuperServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {
    @Autowired
    private UserService userService;
    @Autowired
    private CacheChannel cache;

    private final static String[] BROWSER = new String[]{
            "Chrome", "Firefox", "Microsoft Edge", "Safari", "Opera"
    };
    private final static String[] OPERATING_SYSTEM = new String[]{
            "Android", "Linux", "Mac OS X", "Ubuntu", "Windows 10", "Windows 8", "Windows 7", "Windows XP", "Windows Vista"
    };

    private static String simplifyOperatingSystem(String operatingSystem) {
        for (String b : OPERATING_SYSTEM) {
            if (StrUtil.containsIgnoreCase(operatingSystem, b)) {
                return b;
            }
        }
        return operatingSystem;
    }

    private static String simplifyBrowser(String browser) {
        for (String b : BROWSER) {
            if (StrUtil.containsIgnoreCase(browser, b)) {
                return b;
            }
        }
        return browser;
    }

    @Override
    public LoginLog save(Long userId, String account, String ua, String ip, String location, String description) {
        User user;
        if (userId != null) {
            user = this.userService.getByIdCache(userId);
        } else {
            user = this.userService.getByAccount(account);
        }

        UserAgent userAgent = UserAgent.parseUserAgentString(ua);
        Browser browser = userAgent.getBrowser();
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        LoginLog loginLog = LoginLog.builder()
                .location(location)
                .loginDate(LocalDate.now())
                .description(description)
                .requestIp(ip).ua(ua)
                .browser(simplifyBrowser(browser.getName())).browserVersion(userAgent.getBrowserVersion().getVersion())
                .operatingSystem(simplifyOperatingSystem(operatingSystem.getName()))
                .build();
        if (user != null) {
            loginLog.setAccount(user.getAccount()).setUserId(user.getId()).setUserName(user.getName())
                    .setCreateUser(user.getId());
        }

        super.save(loginLog);
        LocalDate now = LocalDate.now();
        LocalDate tenDays = now.plusDays(-9);
        this.cache.evict(CacheKey.LOGIN_LOG_TOTAL, CacheKey.buildTenantKey());
        this.cache.evict(CacheKey.LOGIN_LOG_TODAY, CacheKey.buildTenantKey(now));
        this.cache.evict(CacheKey.LOGIN_LOG_TODAY_IP, CacheKey.buildTenantKey(now));
        this.cache.evict(CacheKey.LOGIN_LOG_TEN_DAY, CacheKey.buildTenantKey(tenDays, null));
        this.cache.evict(CacheKey.LOGIN_LOG_BROWSER, CacheKey.buildTenantKey());
        this.cache.evict(CacheKey.LOGIN_LOG_SYSTEM, CacheKey.buildTenantKey());
        if (user != null) {
            this.cache.evict(CacheKey.LOGIN_LOG_TEN_DAY, CacheKey.buildTenantKey(tenDays, user.getAccount()));
        }
        return loginLog;
    }

    @Override
    public Long findTotalVisitCount() {
        CacheObject cacheObject = this.cache.get(CacheKey.LOGIN_LOG_TOTAL, CacheKey.buildTenantKey(), (key) -> this.baseMapper.findTotalVisitCount());
        return (Long) cacheObject.getValue();
    }

    @Override
    public Long findTodayVisitCount() {
        LocalDate now = LocalDate.now();
        CacheObject cacheObject = this.cache.get(CacheKey.LOGIN_LOG_TODAY, CacheKey.buildTenantKey(now), (key) -> this.baseMapper.findTodayVisitCount(now));
        return (Long) cacheObject.getValue();
    }

    @Override
    public Long findTodayIp() {
        LocalDate now = LocalDate.now();
        CacheObject cacheObject = this.cache.get(CacheKey.LOGIN_LOG_TODAY_IP, CacheKey.buildTenantKey(now), (key) -> this.baseMapper.findTodayIp(now));
        return (Long) cacheObject.getValue();
    }

    @Override
    public List<Map<String, Object>> findLastTenDaysVisitCount(String account) {
        LocalDate tenDays = LocalDate.now().plusDays(-9);
        CacheObject cacheObject = this.cache.get(CacheKey.LOGIN_LOG_TEN_DAY, CacheKey.buildTenantKey(tenDays, account), (key) -> this.baseMapper.findLastTenDaysVisitCount(tenDays, account));
        return (List<Map<String, Object>>) cacheObject.getValue();
    }

    @Override
    public List<Map<String, Object>> findByBrowser() {
        CacheObject cacheObject = this.cache.get(CacheKey.LOGIN_LOG_BROWSER, CacheKey.buildTenantKey(), (key) -> this.baseMapper.findByBrowser());
        return (List<Map<String, Object>>) cacheObject.getValue();
    }

    @Override
    public List<Map<String, Object>> findByOperatingSystem() {
        CacheObject cacheObject = this.cache.get(CacheKey.LOGIN_LOG_SYSTEM, CacheKey.buildTenantKey(), (key) -> this.baseMapper.findByOperatingSystem());
        return (List<Map<String, Object>>) cacheObject.getValue();
    }

    @Override
    public boolean clearLog(LocalDateTime clearBeforeTime, Integer clearBeforeNum) {
        return baseMapper.clearLog(clearBeforeTime, clearBeforeNum);
    }
}
