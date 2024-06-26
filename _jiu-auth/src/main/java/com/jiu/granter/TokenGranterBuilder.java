package com.jiu.granter;

import com.jiu.exception.BizException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TokenGranterBuilder
 *
 */
@Component
public class TokenGranterBuilder {

    /**
     * TokenGranter缓存池
     */
    private Map<String, TokenGranter> granterPool = new ConcurrentHashMap<>();

    public TokenGranterBuilder(Map<String, TokenGranter> granterPool) {
        granterPool.forEach(this.granterPool::put);
    }

    /**
     * 获取TokenGranter
     *
     * @param grantType 授权类型
     * @return ITokenGranter
     */
    public TokenGranter getGranter(String grantType) {
        TokenGranter tokenGranter = granterPool.get(grantType);
        if (tokenGranter == null) {
            throw new BizException("grantType 不支持，请传递正确的 grantType 参数");
        } else {
            return tokenGranter;
        }
    }

}
