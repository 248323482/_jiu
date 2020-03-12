package com.jiu.web.context;

import com.jiu.utils.ThreadLocalHolder;
import com.jiu.utils.WebUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 验证验证码
 */
public class RegisterUserInterceptor extends HandlerInterceptorAdapter {


    public RegisterUserInterceptor() {
    }

    /*
    * For validate captcha
    * */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        persistIp(request);
        if (RequestMethod.GET.name().equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        final String correctCaptcha = WebUtils.getCaptchaKey(request.getSession());
        final String captcha = ServletRequestUtils.getStringParameter(request, "");

        if (StringUtils.isEmpty(captcha) || !captcha.equalsIgnoreCase(correctCaptcha)) {
            response.getWriter().print("<script>alert('Invalid Captcha.');history.back();</script>");
            return false;
        }

        return true;
    }

    /*
     * 将IP地址 放置在 ThreadLocal 中
     * */
    private void persistIp(HttpServletRequest request) {
        final String clientIp = WebUtils.retrieveClientIp(request);
        ThreadLocalHolder.clientIp(clientIp);
    }

}