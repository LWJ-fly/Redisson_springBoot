package test.redisson.utils.LockUtil.service.impl;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import test.redisson.utils.LockUtil.enums.LockEnum;

import java.util.function.Supplier;

/**
 * 类描述：Redis分布式锁实现
 * @author 技匠
 * @date 2023-03-04 16:07:44
 * 版权所有 Copyright www.wenmeng.online
 */
@Component
public class RedisDistributedLockImpl extends DistributedLockServiceImpl {
    @Autowired
    private RedissonClient redissonClient;
    
    @Override
    public void unLock(LockEnum lockEnum, Object subKey) {
        redissonClient.getLock(getKey(lockEnum, subKey)).unlock();
    }
    
    @Override
    public void lock(LockEnum lockEnum, Object subKey) {
        try {
            redissonClient.getLock(getKey(lockEnum, subKey)).lock();
        } catch (Exception ignore) {
        }
    }
    
    @Override
    public <T> T automaticRenewallock(LockEnum lockEnum, Object subKey, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(getKey(lockEnum, subKey));
        lock.lock();
        try {
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }
}
