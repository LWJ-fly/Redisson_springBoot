package test.redisson.utils.LockUtil.service.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class DistributedLockServiceImpl implements DistributedLockService {
    
    
    /**
     * 枚举线程锁，同一枚举只允许一个线程抢锁 K：锁ID V:线程id
     */
    private final Map<String, Long> lockEnumMap = new HashMap<>();
    /**
     * 枚举线程锁，同一枚举只允许一个线程抢锁 K:锁ID V：枚举加锁次数
     */
    private final Map<String, Integer> lockEnumCountMap = new HashMap<>();
    /**
     * 线程锁
     */
    private final ReentrantLock lock = new ReentrantLock();
    /**
     * 获取配置文件，判断是否启用Redis
     */
    @Value("${redisUsable}")
    private String redisUsable;
    @Autowired
    private SqlDistributedLockImpl sqlLockService;
    
    @Autowired
    private RedisDistributedLockImpl redisLockService;
    
    /**
     * 方法描述：释放锁
     * @param lockEnum 锁的枚举
     * @return {@link Boolean} 是否加锁成功
     * @date 2023-02-14 14:48:49
     */
    @Override
    public void unLock(LockEnum lockEnum) {
        unLock(lockEnum, null);
    }
    
    @Override
    public void unLock(LockEnum lockEnum, Object subKey) {
        try {
            lockFun(lockEnum, subKey, () -> {
                // redis是否可用
                if (Boolean.parseBoolean(redisUsable)) {
                    redisLockService.unLock(lockEnum, subKey);
                } else {
                    sqlLockService.unLock(lockEnum, subKey);
                }
                return false;
            });
        } finally {
            unLockEnum(lockEnum, subKey);
            unLockEnum(lockEnum, subKey);
        }
    }
    
    @Override
    public void lock(LockEnum lockEnum) {
        lock(lockEnum, null);
    }
    
    @Override
    public void lock(LockEnum lockEnum, Object subKey) {
        lockFun(lockEnum, subKey, () -> {
            // redis是否可用
            if (Boolean.parseBoolean(redisUsable)) {
                redisLockService.lock(lockEnum, subKey);
            } else {
                sqlLockService.lock(lockEnum, subKey);
            }
            return null;
        });
    }
    
    @Override
    public <T> T automaticRenewallock(LockEnum lockEnum, Supplier<T> supplier) {
        return automaticRenewallock(lockEnum, null, supplier);
    }
    
    @Override
    public <T> T automaticRenewallock(LockEnum lockEnum, Object subKey, Supplier<T> supplier) {
        try {
            return lockFun(lockEnum, subKey, () -> {
                if (Boolean.parseBoolean(redisUsable)) {
                    return redisLockService.automaticRenewallock(lockEnum, supplier);
                } else {
                    return sqlLockService.automaticRenewallock(lockEnum, subKey, supplier);
                }
            });
        } finally {
            unLockEnum(lockEnum, subKey);
        }
    }
    
    /**
     * 方法描述：阻塞式获取线程锁
     * @param supplier 获取锁后执行事件
     * @return {@link T} 返回类型
     * @date 2023-02-14 11:27:18
     */
    private <T> T lockFun(LockEnum lockEnum, Object subKey, Supplier<T> supplier) {
        while (lockedEnum(lockEnum, subKey)) {
            sleep();
        }
        return supplier.get();
    }
    
    /**
     * 方法描述：校验是否已经被其他线程加锁
     * @param lockEnum 加锁的枚举
     * @param subKey 锁的子码（划分更小锁粒度）
     * @return {@link Boolean} true:已经被其他线程加锁  false:被当前线程加锁
     * @date 2023-03-10 13:33:02
     */
    private Boolean lockedEnum(LockEnum lockEnum, Object subKey) {
        lock.lock();
        try {
            String key = getKey(lockEnum, subKey);
            Thread thread = Thread.currentThread();
            if (!Boolean.parseBoolean(redisUsable)) {
                // 添加定时监控事件
                ThreadMonitorTask.addThread(thread);
            }
            if (lockEnumMap.containsKey(key)) {
                if (!Objects.equals(lockEnumMap.get(key), thread.getId()) && lockEnumCountMap.containsKey(key) && lockEnumCountMap.get(key) > 0) {
                    return true;
                }
                lockEnumCountMap.put(key, lockEnumCountMap.get(key) + 1);
                return false;
            }
            // 设置线程名称  = 锁备注 + 锁编码 + 随机码
            Thread.currentThread().setName(lockEnum.getDesc() + "_" + key + "_" + RandomStringUtils.randomAlphabetic(5));
            lockEnumMap.put(key, thread.getId());
            lockEnumCountMap.put(key, 1);
            return false;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 方法描述：解锁当前线程
     * @param lockEnum 解锁线程枚举
     * @date 2023-03-10 13:26:21
     */
    private void unLockEnum(LockEnum lockEnum, Object subKey) {
        lock.lock();
        try {
            String key = getKey(lockEnum, subKey);
            Thread thread = Thread.currentThread();
            if (lockEnumMap.containsKey(key) && Objects.equals(lockEnumMap.get(key), thread.getId())) {
                if (lockEnumCountMap.get(key) > 1) {
                    lockEnumCountMap.put(key, lockEnumCountMap.get(key) - 1);
                    return;
                }
                thread.setName(RandomStringUtils.randomAlphabetic(20));
                lockEnumMap.remove(key);
                lockEnumCountMap.remove(key);
                // 移除定时监控事件
                ThreadMonitorTask.removeThread(thread);
            }
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
    
    protected String getKey(LockEnum lockEnum, Object subKey) {
        if (lockEnum == null && subKey == null) {
            return null;
        }
        if (lockEnum != null && subKey == null) {
            return String.valueOf(lockEnum.getCode());
        }
        if (lockEnum == null) {
            String key = JSON.toJSONString(subKey);
            if (key.length() > 50) {
                key = key.substring(0, 49);
            }
            return key;
        }
        String key = lockEnum.getCode() + "_" + JSON.toJSONString(subKey);
        if (key.length() > 50) {
            key = key.substring(0, 49);
        }
        return key;
    }
}
