package test.redisson.utils.LockUtil.service;

import test.redisson.utils.LockUtil.enums.LockEnum;

import java.util.function.Supplier;

public interface DistributedLockService {
    
    /**
     * 方法描述：获取锁，非阻塞式锁
     * @return {@link boolean}  true:获取锁成功  、 false：获取锁失败
     * @date 2023-02-13 17:05:58
     */
    Boolean getLock(LockEnum lockEnum);
    
    /**
     * 方法描述：释放锁
     * @param lockEnum 锁的枚举
     * @return {@link Boolean} 是否加锁成功
     * @date 2023-02-14 14:48:49
     */
    Boolean unLock(LockEnum lockEnum);
    
    /**
     * 方法描述：阻塞式获取锁
     * @param lockEnum 锁枚举
     * @date 2023-02-14 17:19:05
     */
    void blockingAcquireLock(LockEnum lockEnum);
    
    /**
     * 方法描述：半阻塞式获取锁
     * @param lockEnum 锁枚举
     * @param waitTime 等待加锁时长，超时加锁失败，则返回false
     * @return {@link Boolean} true：获取锁成功、 false：获取锁失败
     * @date 2023-02-14 17:18:07
     */
    Boolean semiBlockingAcquireLock(LockEnum lockEnum, Long waitTime);
    
    /**
     * 方法描述：封装锁函数--自动续费锁
     * @param lockEnum 锁枚举
     * @param supplier 获取锁后的动作
     * @return {@link T} 执行完之后返回的值
     * @date 2023-02-14 17:32:32
     */
    <T> T automaticRenewallock(LockEnum lockEnum, Supplier<T> supplier);
}
