package test.redisson.utils.LockUtil.service.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.redisson.utils.LockUtil.enums.LockEnum;
import test.redisson.utils.LockUtil.monitorTask.ThreadMonitorTask;
import test.redisson.utils.LockUtil.service.DistributedLockService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * 类描述：分布式锁实体类
 * @author 技匠
 * @date 2023-03-04 15:54:36
 * 版权所有 Copyright www.wenmeng.online
 */
@Service
public class DistributedLockserviceImpl implements DistributedLockService {
    
    /**
     * 线程锁
     */
    private final ReentrantLock lock = new ReentrantLock();
    /**
     * 枚举线程锁，同一枚举只允许一个线程抢锁 K：枚举code  V:线程id
     */
    private static final Map<Integer, Long> LOCK_ENUM_MAP = new HashMap<>();
    /**
     * 枚举线程锁，同一枚举只允许一个线程抢锁 K:枚举code V:锁次数
     */
    private static final Map<Integer, Integer> LOCK_ENUM_COUNT_MAP = new HashMap<>();
    
    @Autowired(required = false)
    private SqlDistributedLockImpl sqlLockService;
    
    @Autowired(required = false)
    private RedisDistributedLockImpl redisLockService;
    /**
     * 获取配置文件，判断是否启用Redis
     */
    private final Boolean redisUsable = true;
    
    @Override
    public Boolean tryLock(LockEnum lockEnum) {
        return tryLockThreadAndCodeFun(lockEnum, () -> {
            // redis是否可用
            if (redisUsable) {
                return redisLockService.tryLock(lockEnum);
            } else {
                return sqlLockService.tryLock(lockEnum);
            }
        });
    }
    
    @Override
    public void unLock(LockEnum lockEnum) {
        lockThreadAndCodeFun(lockEnum, ()->{
            // redis是否可用
            if (redisUsable) {
                redisLockService.unLock(lockEnum);
            } else {
                sqlLockService.unLock(lockEnum);
            }
            unLockEnum(lockEnum);
            return null;
        });
    }
    
    @Override
    public void lock(LockEnum lockEnum) {
        lockThreadAndCodeFun(lockEnum, ()->{
            // redis是否可用
            if (redisUsable) {
                redisLockService.lock(lockEnum);
            } else {
                sqlLockService.lock(lockEnum);
            }
            unLockEnum(lockEnum);
            return null;
        });
    }
    
    @Override
    public <T> T automaticRenewallock(LockEnum lockEnum, Supplier<T> supplier) {
        try {
            return lockThreadAndCodeFun(lockEnum, () -> {
                if (redisUsable) {
                    return redisLockService.automaticRenewallock(lockEnum, supplier);
                } else {
                    return sqlLockService.automaticRenewallock(lockEnum, supplier);
                }
            });
        } finally {
            unLockEnum(lockEnum);
        }
    }
    
    
    
    /**
     * 方法描述：非阻塞式获取线程锁
     * @param supplier 获取锁后执行事件
     * @return {@link Boolean} 是否获取锁成功
     * @date 2023-02-14 11:28:04
     */
    private Boolean tryLockThreadAndCodeFun(LockEnum lockEnum, Supplier<Boolean> supplier) {
        if (lockedEnum(lockEnum)) {
            return supplier.get();
        }
        return false;
    }
    
    
    /**
     * 方法描述：阻塞式获取线程锁
     * @param supplier 获取锁后执行事件
     * @return {@link T} 返回类型
     * @date 2023-02-14 11:27:18
     */
    private <T> T lockThreadAndCodeFun(LockEnum lockEnum, Supplier<T> supplier) {
        try {
            while (!lockedEnum(lockEnum)) {
                sleep();
            }
            return supplier.get();
        } finally {
            unLockEnum(lockEnum);
        }
    }
    
    /**
     * 方法描述：解锁枚举
     * @param lockEnum 枚举值
     * @date 2023-03-10 15:30:50
     */
    private void unLockEnum(LockEnum lockEnum) {
        lock.lock();
        try {
            Integer key = lockEnum.getCode();
            Thread thread = Thread.currentThread();
            if (LOCK_ENUM_MAP.containsKey(key) && Objects.equals(LOCK_ENUM_MAP.get(key), thread.getId())) {
                if (LOCK_ENUM_COUNT_MAP.get(key) > 1) {
                    LOCK_ENUM_COUNT_MAP.put(key,LOCK_ENUM_COUNT_MAP.get(key) - 1);
                    return;
                }
                if (!redisUsable) {
                    ThreadMonitorTask.removeThread(thread);
                }
                LOCK_ENUM_MAP.remove(key);
                LOCK_ENUM_COUNT_MAP.remove(key);
            }
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 方法描述：锁枚举值
     * @param lockEnum 枚举值
     * @return {@link Boolean} true：锁成功    false：锁失败
     * @date 2023-03-10 15:29:39
     */
    private Boolean lockedEnum(LockEnum lockEnum) {
        lock.lock();
        try {
            Integer key = lockEnum.getCode();
            Thread thread = Thread.currentThread();
            if (LOCK_ENUM_MAP.containsKey(key)) {
                if (!Objects.equals(LOCK_ENUM_MAP.get(key), thread.getId())) {
                    return false;
                }
                LOCK_ENUM_COUNT_MAP.put(key, LOCK_ENUM_COUNT_MAP.get(key) + 1);
                return true;
            }
            // 设置线程名称  = 锁备注 + 锁编码 + 随机码
            Thread.currentThread().setName(lockEnum.getDesc() + "_" + lockEnum.getCode() + "_" + RandomStringUtils.randomAlphabetic(5));
            LOCK_ENUM_MAP.put(key, thread.getId());
            if (!redisUsable) {
                ThreadMonitorTask.addThread(thread);
            }
            return true;
        } finally {
            lock.unlock();
        }
    }
    
    public void sleep() {
        try {
            Thread.sleep(200);
        } catch (Exception ignored) {
        }
    }
}
