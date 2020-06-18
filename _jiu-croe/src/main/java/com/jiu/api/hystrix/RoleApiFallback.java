package com.jiu.api.hystrix;

import com.jiu.api.RoleApi;
import com.jiu.base.R;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 角色查询API
 *
 */
@Component
public class RoleApiFallback implements RoleApi {
    @Override
    public R<List<Long>> findUserIdByCode(String[] codes) {
        return R.timeout();
    }
}
