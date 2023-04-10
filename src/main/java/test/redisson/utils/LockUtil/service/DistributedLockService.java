package test.redisson.utils.LockUtil.service;

import test.redisson.utils.LockUtil.enums.LockEnum;

import java.util.function.Supplier;

public interface DistributedLockService {
    
    /**
     * 方法描述：释放锁
     * @param lockEnum 锁的枚举
     * @return {@link Boolean} 是否加锁成功
     * @date 2023-02-14 14:48:49
     */
    void unLock(LockEnum lockEnum);
    
    /**
     * 方法描述：释放锁
     * @param lockEnum 锁的枚举
     * @param subKey 锁的详细Key（划分更小锁粒度）
     * @return {@link Boolean} 是否加锁成功
     * @date 2023-02-14 14:48:49
     */
    void unLock(LockEnum lockEnum, Object subKey);
    
    /**
     * 方法描述：阻塞式获取锁
     * @param lockEnum 锁枚举
     * @date 2023-02-14 17:19:05
     */
    void lock(LockEnum lockEnum);
    
    /**
     * 方法描述：阻塞式获取锁
     * @param lockEnum 锁枚举
     * @param subKey 锁的详细Key（划分更小锁粒度）
     * @date 2023-02-14 17:19:05
     */
    void lock(LockEnum lockEnum, Object subKey);
    
    /**
     * 方法描述：封装锁函数--自动续费锁
     * @param lockEnum 锁枚举
     * @param supplier 获取锁后的动作
     * @return {@link T} 执行完之后返回的值
     * @date 2023-02-14 17:32:32
     */
    <T> T automaticRenewallock(LockEnum lockEnum, Supplier<T> supplier);
    
    /**
     * 方法描述：封装锁函数--自动续费锁
     * @param lockEnum 锁枚举
     * @param subKey 锁的详细Key（划分更小锁粒度）
     * @param supplier 获取锁后的动作
     * @return {@link T} 执行完之后返回的值
     * @date 2023-02-14 17:32:32
     */
    <T> T automaticRenewallock(LockEnum lockEnum, Object subKey, Supplier<T> supplier);
}
