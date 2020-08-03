package com.jiu.j2cache;
 
import net.oschina.j2cache.CacheChannel;
import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
 
import java.util.Collection;
 
/**
 */
public class J2CacheSpringCacheManageAdapter extends AbstractTransactionSupportingCacheManager {
    /**
     * Load the initial caches for this cache manager.
     * <p>Called by {@link #afterPropertiesSet()} on startup.
     * The returned collection may be empty but must not be {@code null}.
     */
    @Override
    protected Collection<? extends Cache> loadCaches() {
        return null;
    }
 
    private CacheChannel cacheChannel;
 
    private boolean allowNullValues;
 
    /**
     * @param allowNullValues 默认 true
     */
    J2CacheSpringCacheManageAdapter(CacheChannel cacheChannel, boolean allowNullValues) {
        this.cacheChannel = cacheChannel;
        this.allowNullValues = allowNullValues;
    }
 
    @Override
    protected Cache getMissingCache(String name) {
        return new J2CacheSpringCacheAdapter(allowNullValues, cacheChannel, name);
    }
}