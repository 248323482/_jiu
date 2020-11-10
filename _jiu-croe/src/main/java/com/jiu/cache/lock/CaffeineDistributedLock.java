package com.jiu.cache.lock;


import com.jiu.lock.AbstractDistributedLock;

/**
 * 分布式锁 只能用redis实现
 * 写这个类的目的，只是为了防止代码启动报错
 *
 */
public class CaffeineDistributedLock extends AbstractDistributedLock {
    @Override
    public boolean lock(String key, long expire, int retryTimes, long sleepMillis) {
        return true;
    }

    @Override
    public boolean releaseLock(String key) {
        return true;
    }
}
