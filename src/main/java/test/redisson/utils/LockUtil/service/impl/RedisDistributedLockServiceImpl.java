package test.redisson.utils.LockUtil.service.impl;

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
public class RedisDistributedLockServiceImpl implements DistributedLockService {
    @Override
    public Boolean getLock(LockEnum lockEnum) {
        return null;
    }
    
    @Override
    public Boolean unLock(LockEnum lockEnum) {
        return null;
    }
    
    @Override
    public void blockingAcquireLock(LockEnum lockEnum) {
    
    }
    
    @Override
    public Boolean semiBlockingAcquireLock(LockEnum lockEnum, Long waitTime) {
        return null;
    }
    
    @Override
    public <T> T automaticRenewallock(LockEnum lockEnum, Supplier<T> supplier) {
        return null;
    }
}
