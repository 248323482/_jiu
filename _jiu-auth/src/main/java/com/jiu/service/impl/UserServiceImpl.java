package com.jiu.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiu.base.service.SuperCacheServiceImpl;
import com.jiu.dao.UserMapper;
import com.jiu.database.mybatis.conditions.query.LbqWrapper;
import com.jiu.dto.UserUpdatePasswordDTO;
import com.jiu.entity.User;
import com.jiu.security.feign.UserQuery;
import com.jiu.security.model.SysUser;
import com.jiu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * <p>
 * 业务实现类
 * 账号
 * </p>
 *
 */
@Slf4j
@Service

public class UserServiceImpl extends SuperCacheServiceImpl<UserMapper, User> implements UserService {


    @Override
    protected String getRegion() {
        return null;
    }

    @Override
    public Map<String, Object> getDataScopeById(Long userId) {
        return null;
    }

    @Override
    public List<User> findUserByRoleId(Long roleId, String keyword) {
        return null;
    }

    @Override
    public boolean check(String account) {
        return false;
    }

    @Override
    public void incrPasswordErrorNumById(Long id) {

    }

    @Override
    public User getByAccount(String account) {
        return null;
    }

    @Override
    public User saveUser(User user) {
        return null;
    }

    @Override
    public boolean reset(List<Long> ids) {
        return false;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public boolean remove(List<Long> ids) {
        return false;
    }

    @Override
    public IPage<User> findPage(IPage<User> page, LbqWrapper<User> wrapper) {
        return null;
    }

    @Override
    public Boolean updatePassword(UserUpdatePasswordDTO data) {
        return null;
    }

    @Override
    public int resetPassErrorNum(Long id) {
        return 0;
    }

    @Override
    public Map<Serializable, Object> findUserByIds(Set<Serializable> ids) {
        return null;
    }

    @Override
    public Map<Serializable, Object> findUserNameByIds(Set<Serializable> ids) {
        return null;
    }

    @Override
    public SysUser getSysUserById(Long id, UserQuery query) {
        return null;
    }

    @Override
    public List<Long> findAllUserId() {
        return null;
    }

    @Override
    public boolean initUser(User user) {
        return false;
    }
}
