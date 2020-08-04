package com.jiu.service;

import com.jiu.base.service.SuperService;
import com.jiu.dto.RoleAuthoritySaveDTO;
import com.jiu.dto.UserRoleSaveDTO;
import com.jiu.entity.RoleAuthority;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 角色的资源
 * </p>
 *
 */
public interface RoleAuthorityService extends SuperService<RoleAuthority> {

    /**
     * 给用户分配角色
     *
     * @param userRole
     * @return
     */
    boolean saveUserRole(UserRoleSaveDTO userRole);

    /**
     * 给角色重新分配 权限（资源/菜单）
     *
     * @param roleAuthoritySaveDTO
     * @return
     */
    boolean saveRoleAuthority(RoleAuthoritySaveDTO roleAuthoritySaveDTO);

    /**
     * 根据权限id 删除
     *
     * @param ids
     * @return
     */
    boolean removeByAuthorityId(List<Long> ids);
}
