package com.jiu.service.common;

import com.jiu.base.service.SuperCacheService;
import com.jiu.entity.common.DictionaryItem;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 业务接口
 * 字典项
 * </p>
 *
 */
public interface DictionaryItemService extends SuperCacheService<DictionaryItem> {
    /**
     * 根据类型查询字典
     *
     * @param types
     * @return
     */
    Map<String, Map<String, String>> map(String[] types);

    /**
     * 根据类型编码查询字典项
     * @param codes
     * @return
     */
    Map<Serializable, Object> findDictionaryItem(Set<Serializable> codes);
}
