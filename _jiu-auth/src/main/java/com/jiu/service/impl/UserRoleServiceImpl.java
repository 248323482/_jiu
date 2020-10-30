package com.jiu.service.impl;


import com.jiu.base.service.SuperServiceImpl;
import com.jiu.dao.RoleMapper;
import com.jiu.dao.UserRoleMapper;
import com.jiu.database.mybatis.conditions.Wraps;
import com.jiu.entity.Role;
import com.jiu.entity.UserRole;
import com.jiu.exception.BizException;
import com.jiu.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jiu.common.constant.BizConstant.INIT_ROLE_CODE;

/**
 * <p>
 * 业务实现类
 * 角色分配
 * 账号角色绑定
 * </p>
 *
 */
@Slf4j
@Service

public class UserRoleServiceImpl extends SuperServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initAdmin(Long userId) {
        Role role = roleMapper.selectOne(Wraps.<Role>lbQ().eq(Role::getCode, INIT_ROLE_CODE));
        if (role == null) {
            throw BizException.wrap("初始化用户角色失败, 无法查询到内置角色:%s", INIT_ROLE_CODE);
        }
        UserRole userRole = UserRole.builder()
                .userId(userId).roleId(role.getId())
                .build();

        return super.save(userRole);
    }
}
