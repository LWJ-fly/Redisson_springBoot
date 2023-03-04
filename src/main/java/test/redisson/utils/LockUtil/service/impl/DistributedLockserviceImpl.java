package test.redisson.utils.LockUtil.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import test.redisson.utils.LockUtil.enums.LockEnum;
import test.redisson.utils.LockUtil.service.DistributedLockService;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * 类描述：分布式锁实体类
 * @author 技匠
 * @date 2023-03-04 15:54:36
 * 版权所有 Copyright www.wenmeng.online
 */
public class DistributedLockserviceImpl implements DistributedLockService {
    
    /**
     * 线程锁
     */
    private final ReentrantLock lock = new ReentrantLock();
    @Autowired(required = false)
    private SqlDistributedLockServiceImpl sqlLockService;
    @Autowired(required = false)
    private RedisDistributedLockServiceImpl redisLockService;
    /**
     * 获取配置文件，判断是否启用Redis
     */
    private final Boolean redisUsable = false;
    
    @Override
    public Boolean getLock(LockEnum lockEnum) {
        return tryLockFun(() -> {
            // redis是否可用
            if (redisUsable) {
                return redisLockService.getLock(lockEnum);
            } else {
                return sqlLockService.getLock(lockEnum);
            }
        });
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
    
    /**
     * 方法描述：非阻塞式获取线程锁
     * @param supplier 获取锁后执行事件
     * @return {@link Boolean} 是否获取锁成功
     * @date 2023-02-14 11:28:04
     */
    private Boolean tryLockFun(Supplier<Boolean> supplier) {
        if (!lock.tryLock()) {
            return false;
        }
        try {
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }
}
