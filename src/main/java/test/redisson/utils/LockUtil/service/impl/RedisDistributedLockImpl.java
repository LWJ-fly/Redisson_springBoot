package test.redisson.utils.LockUtil.service.impl;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import test.redisson.utils.LockUtil.enums.LockEnum;
import test.redisson.utils.LockUtil.service.DistributedLockService;

import java.util.function.Supplier;

/**
 * 类描述：Redis分布式锁实现
 * @author 技匠
 * @date 2023-03-04 16:07:44
 * 版权所有 Copyright www.wenmeng.online
 */
@Component
public class RedisDistributedLockImpl implements DistributedLockService {
    @Autowired
    private RedissonClient redissonClient;
    
    @Override
    public Boolean tryLock(LockEnum lockEnum) {
        return redissonClient.getLock(String.valueOf(lockEnum.getCode())).tryLock();
    }
    
    @Override
    public void unLock(LockEnum lockEnum) {
        try {
            redissonClient.getLock(String.valueOf(lockEnum.getCode())).unlock();
        } catch (Exception ignore) { }
    }
    
    @Override
    public void lock(LockEnum lockEnum) {
    
    }
    
    @Override
    public <T> T automaticRenewallock(LockEnum lockEnum, Supplier<T> supplier) {
        return null;
    }
}
