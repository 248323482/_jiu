package com.jiu.api;

import com.jiu.api.hystrix.LogApiFallback;
import com.jiu.base.R;
import com.jiu.log.entity.OptLogDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 操作日志保存 API
 *
 */
@FeignClient(name = "jiu-common-server", fallback = LogApiFallback.class, qualifier = "logApi")
public interface LogApi {

    /**
     * 保存日志
     *
     * @param log 日志
     * @return
     */
    @RequestMapping(value = "/optLog", method = RequestMethod.POST)
    R<OptLogDTO> save(@RequestBody OptLogDTO log);

}
