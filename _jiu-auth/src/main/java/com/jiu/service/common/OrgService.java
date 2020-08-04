package com.jiu.service.common;

import com.jiu.base.service.SuperCacheService;
import com.jiu.entity.common.Org;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 业务接口
 * 组织
 * </p>
 *
 */
public interface OrgService extends SuperCacheService<Org> {
    /**
     * 查询指定id集合下的所有子集
     *
     * @param ids
     * @return
     */
    List<Org> findChildren(List<Long> ids);

    /**
     * 批量删除以及删除其子节点
     *
     * @param ids
     * @return
     */
    boolean remove(List<Long> ids);

    /**
     * 根据 id 查询组织，并转换成Map结构
     *
     * @param ids
     * @return
     */
    Map<Serializable, Object> findOrgByIds(Set<Serializable> ids);

    /**
     * 根据 id 查询名称，并转换成Map结构
     *
     * @param ids
     * @return
     */
    Map<Serializable, Object> findOrgNameByIds(Set<Serializable> ids);
}
