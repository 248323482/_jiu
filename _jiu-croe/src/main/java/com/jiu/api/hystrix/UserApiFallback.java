package com.jiu.api.hystrix;

import com.jiu.api.UserApi;
import com.jiu.base.R;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

/**
 * 用户API熔断
 *
 */
@Component
public class UserApiFallback implements UserApi {
    @Override
    public Map<String, Object> getDataScopeById(Long id) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("dsType", 5);
        map.put("orgIds", Collections.emptyList());
        return map;
    }

    @Override
    public Map<Serializable, Object> findUserByIds(Set<Serializable> codes) {
        return Collections.emptyMap();
    }

    @Override
    public Map<Serializable, Object> findUserNameByIds(Set<Serializable> codes) {
        return Collections.emptyMap();
    }

    @Override
    public R findAllUserId() {
        return R.timeout();
    }
}
