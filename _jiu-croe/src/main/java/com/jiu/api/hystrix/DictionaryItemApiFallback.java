package com.jiu.api.hystrix;

import com.jiu.api.DictionaryItemApi;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 数据字典项 查询
 */
@Component
public class DictionaryItemApiFallback implements DictionaryItemApi {

    @Override
    public Map<Serializable, Object> findDictionaryItem(Set<Serializable> codes) {
        return new HashMap<>(1);
    }
}
