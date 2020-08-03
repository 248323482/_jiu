package com.jiu.dao;

import com.jiu.base.mapper.SuperMapper;
import com.jiu.entity.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 角色
 * </p>
 *
 */
@Repository
public interface RoleMapper extends SuperMapper<Role> {
    /**
     * 查询用户拥有的角色
     *
     * @param userId
     * @return
     */
    List<Role> findRoleByUserId(@Param("userId") Long userId);

    /**
     * 根据角色编码查询用户ID
     *
     * @param codes 角色编码
     * @return
     */
    List<Long> findUserIdByCode(@Param("codes") String[] codes);
}
