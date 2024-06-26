package com.jiu.security.model;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode
public class SysRole {
    private static final long serialVersionUID = 1L;
    private Long id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 功能描述
     */
    private String describe;

    /**
     * 是否启用
     */
    private Boolean isEnable;

    /**
     * 是否只读角色
     */
    private Boolean isReadonly;

    /**
     * 角色列表转换成角色编码列表
     *
     * @param list
     * @return
     */
    public static List<String> getRoleCode(List<SysRole> list) {
        if (ArrayUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(SysRole::getCode).collect(Collectors.toList());
    }

    /**
     * 指定角色编码是否在角色列表中
     *
     * @param list
     * @return
     */
    public static boolean contains(List<SysRole> list, String code) {
        if (ArrayUtil.isEmpty(list) || StrUtil.isEmpty(code)) {
            return false;
        }
        return list.stream().anyMatch((item) -> code.equals(item.getCode()));
    }
}
