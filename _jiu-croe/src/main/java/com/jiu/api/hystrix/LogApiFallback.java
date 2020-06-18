package com.jiu.api.hystrix;


import com.jiu.api.LogApi;
import com.jiu.base.R;
import com.jiu.log.entity.OptLogDTO;
import org.springframework.stereotype.Component;

/**
 * 日志操作 熔断
 *
 */
@Component
public class LogApiFallback implements LogApi {
    @Override
    public R<OptLogDTO> save(OptLogDTO log) {
        return R.timeout();
    }
}
