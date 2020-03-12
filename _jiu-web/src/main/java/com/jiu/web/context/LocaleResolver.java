package com.jiu.web.context;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public class LocaleResolver extends AcceptHeaderLocaleResolver {
    public static final String LOCALE_SESSION_ATTRIBUTE_NAME = LocaleResolver.class.getName() + ".LOCALE";

    @Override
    public Locale resolveLocale(HttpServletRequest request) {

        Locale locale = getLocale(request);

        if (locale == null) {
            locale = (Locale) request.getSession().getAttribute(LOCALE_SESSION_ATTRIBUTE_NAME);
        } else {
            request.getSession().setAttribute(LOCALE_SESSION_ATTRIBUTE_NAME, locale);
        }

        if (locale != null) {
            return locale;
        }


        return super.resolveLocale(request);
    }

    private Locale getLocale(HttpServletRequest request) {
        final String lang = request.getParameter("__locale");
        if (StringUtils.isNotBlank(lang)) {
            if ("zh_CN".equalsIgnoreCase(lang)) {
                return Locale.SIMPLIFIED_CHINESE;
            } else {
                return Locale.US;
            }
        }
        return null;
    }
}
