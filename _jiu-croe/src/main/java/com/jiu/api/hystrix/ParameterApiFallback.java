package com.jiu.api.hystrix;

import com.jiu.api.ParameterApi;
import com.jiu.base.R;
import org.springframework.stereotype.Component;


/**
 * 熔断类
 *
 */
@Component
public class ParameterApiFallback implements ParameterApi {
    @Override
    public R<String> getValue(String key, String defVal) {
        return R.timeout();
    }
}
