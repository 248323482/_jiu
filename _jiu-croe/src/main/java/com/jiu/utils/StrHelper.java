package com.jiu.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

/**
 * 字符串帮助类
 *
 */
@Slf4j
public class StrHelper {
    public static String getOrDef(String val, String def) {
        return StrUtil.isEmpty(val) ? def : val;
    }

    /**
     * 有 任意 一个 Blank
     *
     * @param css CharSequence
     * @return boolean
     */
    public static boolean isAnyBlank(final CharSequence... css) {
        if (ObjectUtil.isEmpty(css)) {
            return true;
        }
        return Stream.of(css).anyMatch(StrUtil::isBlank);
    }

    /**
     * 是否全非 Blank
     *
     * @param css CharSequence
     * @return boolean
     */
    public static boolean isNoneBlank(final CharSequence... css) {
        if (ObjectUtil.isEmpty(css)) {
            return false;
        }
        return Stream.of(css).allMatch(StrUtil::isNotBlank);
    }


}
