package com.jiu.api.hystrix;

import com.jiu.api.JobsTimingApi;
import com.jiu.base.R;
import org.springframework.stereotype.Component;

/**
 * 定时API 熔断
 *
 */
@Component
public class JobsTimingApiFallback implements JobsTimingApi {
    @Override
    public R<String> addTimingTask(Object xxlJobInfo) {
        return R.timeout();
    }
}
