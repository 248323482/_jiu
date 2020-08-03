package com.jiu.dao;

import com.jiu.base.mapper.SuperMapper;
import com.jiu.entity.Menu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 菜单
 * </p>
 *

 */
@Repository
public interface MenuMapper extends SuperMapper<Menu> {

    /**
     * 查询用户可用菜单
     *
     * @param userId
     * @return
     */
    List<Menu> findVisibleMenu(@Param("userId") Long userId);
}
