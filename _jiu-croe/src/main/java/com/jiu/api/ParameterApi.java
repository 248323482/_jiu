package com.jiu.api;


import com.jiu.api.hystrix.ParameterApiFallback;
import com.jiu.base.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 参数API
 *
 */
@FeignClient(name = "jiu-common-server", path = "/parameter",
        qualifier = "parameterApi", fallback = ParameterApiFallback.class)
public interface ParameterApi {

    /**
     * 根据参数键查询参数值
     *
     * @param key    参数键
     * @param defVal 参数值
     * @return
     */
    @GetMapping("/value")
    R<String> getValue(@RequestParam(value = "key") String key, @RequestParam(value = "defVal") String defVal);
}
