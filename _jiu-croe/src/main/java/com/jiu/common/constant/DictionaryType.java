package com.jiu.common.constant;

/**
 * 存放系统中常用的类型
 *
 */
public class DictionaryType {
    /**
     * 职位状态
     */
    public static final String POSITION_STATUS = "POSITION_STATUS";
    /**
     * 民族
     */
    public static final String NATION = "NATION";
    /**
     * 学历
     */
    public static final String EDUCATION = "EDUCATION";
    /**
     * 行政区级
     */
    public static final String AREA_LEVEL = "AREA_LEVEL";
    public static final String[] ALL = new String[]{
            EDUCATION, NATION, POSITION_STATUS, AREA_LEVEL
    };

    private DictionaryType() {
    }

}
