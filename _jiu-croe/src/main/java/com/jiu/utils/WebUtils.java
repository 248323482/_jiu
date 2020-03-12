package com.jiu.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * web工具类
 */
public abstract class WebUtils {
    /**
     * 验证码 存在SESSION中的变量名
     */
    private static final String CAPTCHA_FORMAT = "%s_Captcha_";


    private WebUtils() {
    }


    /*
     * 向SESSION中设置 验证码的值
     * */
    public static void setCaptchaKey(HttpSession session, String captchaCode) {
        session.setAttribute(captchaSessionKey(session), captchaCode);
    }

    private static String captchaSessionKey(HttpSession session) {
        return String.format(CAPTCHA_FORMAT, session.getId());
    }

    /**
     * 从SESSION中获取 验证码的值
     *
     * @param session HttpSession
     * @return 验证码
     */
    public static String getCaptchaKey(HttpSession session) {
        return (String) session.getAttribute(captchaSessionKey(session));
    }


    /**
     * 向 Response 中写 JSON 数据
     *
     * @param response HttpServletResponse
     * @param json     JSON
     */
//    public static void writeJson(HttpServletResponse response, JSON json) {
//
//        response.setContentType("application/json;charset=" + Application.ENCODING);
//        try {
//            PrintWriter writer = response.getWriter();
//            json.write(writer);
//            writer.flush();
//        } catch (IOException e) {
//            throw new IllegalStateException("Write json to response error", e);
//        }
//
//    }


    /**
     * Retrieve client ip address
     * 获取请求时的 客户端(浏览器) IP地址
     *
     * @param request HttpServletRequest
     * @return IP
     */
    public static String retrieveClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (isUnAvailableIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isUnAvailableIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isUnAvailableIp(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private static boolean isUnAvailableIp(String ip) {
        return StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip);
    }
}
