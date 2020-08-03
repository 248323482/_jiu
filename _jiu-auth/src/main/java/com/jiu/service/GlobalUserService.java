package com.jiu.service;


import com.jiu.base.service.SuperService;
import com.jiu.dto.GlobalUserSaveDTO;
import com.jiu.dto.GlobalUserUpdateDTO;
import com.jiu.entity.GlobalUser;

/**
 * <p>
 * 业务接口
 * 全局账号
 * </p>
 *
 */
public interface GlobalUserService extends SuperService<GlobalUser> {

    /**
     * 检测账号是否可用
     *
     * @param account
     * @return
     */
    Boolean check(String account);

    /**
     * 新建用户
     *
     * @param data
     * @return
     */
    GlobalUser save(GlobalUserSaveDTO data);


    /**
     * 修改
     *
     * @param data
     * @return
     */
    GlobalUser update(GlobalUserUpdateDTO data);
}
