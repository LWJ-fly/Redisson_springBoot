package test.redisson.utils.LockUtil.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.SystemClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import test.redisson.enums.DeleteStatus;
import test.redisson.utils.LocalIpUtil;
import test.redisson.utils.LockUtil.entity.DistributedLock;
import test.redisson.utils.LockUtil.enums.LockEnum;
import test.redisson.utils.LockUtil.service.DistributedLockService;
import test.redisson.utils.LockUtil.sqlService.SqlDistributedLockService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 类描述：数据库实现分布式锁
 * @author 技匠
 * @date 2023-03-04 16:04:28
 * 版权所有 Copyright www.wenmeng.online
 */
@Component
public class SqlDistributedLockServiceImpl implements DistributedLockService {
    
    @Autowired
    private SqlDistributedLockService sqlDistributedLockService;
    
    
    @Override
    public Boolean getLock(LockEnum lockEnum) {
        
        // 查询指定锁
        DistributedLock distributedLock = read(lockEnum);
        // 如果锁已被
        return lockDb(distributedLock, lockEnum, 1000L);
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
    
    private static final Long expirationTime = 3 * 10 * 1000L;
    
    @Scheduled(fixedDelay = 10 * 1000)
    public void guardDogTask() {
        if (lockThreads.isEmpty()) {
            return;
        }
        
        // 查询当前数据库中本机获取锁的信息  本机 && 锁未过期 && 锁未删除 &&
        long now = SystemClock.now();
        List<DistributedLock> distributedLocks = sqlDistributedLockService.list(new QueryWrapper<DistributedLock>()
                .eq("hostIpv6", LocalIpUtil.getInet6Address())
                .le("expirationTime", now)
                .eq("deleteStatus", DeleteStatus.NORMAL.getCode()));
        
        Map<Long, Thread> threadMap = lockThreads.stream().collect(Collectors.toMap(Thread::getId, e -> e));
        for (DistributedLock lock : distributedLocks) {
            if (!threadMap.containsKey(lock.getLockThreadId())) {
                // 当前线程队列中不包含此锁， 可能是同一服务器上运行多个实例，因此不进行处理
                continue;
            }
            Thread thread = threadMap.get(lock.getLockThreadId());
            // 线程持有锁，并且线程存活
            if (Objects.equals(thread.getName(), lock.getLockThreadName()) && thread.isAlive()) {
                // 给当前锁设置过期时间为定时任务执行的三倍
                lock.setExpirationTime(now + expirationTime);
            } else {
                lock.setDeleteStatus(DeleteStatus.DELETED.getCode());
            }
        }
        sqlDistributedLockService.updateBatchById(distributedLocks);
    }
    
    // 获取锁的线程集合
    private static final CopyOnWriteArrayList<Thread> lockThreads = new CopyOnWriteArrayList<>();
    
    /**
     * 方法描述：添加线程到监控
     * @param thread 被监控线程
     * @date 2023-03-04 17:08:51
     */
    public static void addThread(Thread thread) {
        for (Thread lockThread : lockThreads) {
            if (Objects.equals(lockThread.getId(), thread.getId())) {
                lockThreads.remove(lockThread);
                break;
            }
        }
        lockThreads.add(thread);
    }
    
    /**
     * 方法描述：移除线程到监控
     * @param threadId 被监控线程的Id
     * @date 2023-03-04 17:08:51
     */
    public static void removeThread(Long threadId) {
        for (Thread lockThread : lockThreads) {
            if (Objects.equals(lockThread.getId(), threadId)) {
                lockThreads.remove(lockThread);
                break;
            }
        }
    }
    
    /**
     * 方法描述：读取锁枚举
     * @param lockEnum 锁的枚举
     * @return {@link DistributedLock} 锁信息
     * @date 2023-03-04 17:17:16
     */
    private DistributedLock read(LockEnum lockEnum) {
        return sqlDistributedLockService.getById(lockEnum.getCode());
    }
    
    
    /**
     * 方法描述：给当前实体类加锁
     * @param distributedLock 实体类信息
     * @param lockEnum 锁的枚举
     * @param keepLockTime 锁定时间长度
     * @return {@link Boolean} 是否加锁成功
     * @date 2023-02-14 14:48:49
     */
    private Boolean lockDb(DistributedLock distributedLock, LockEnum lockEnum, Long keepLockTime) {
        if (isLocked(distributedLock, lockEnum)) {
            return false;
        }
        if (distributedLock == null) {
            distributedLock = read(lockEnum);
        }
        return sqlDistributedLockService.update(new UpdateWrapper<DistributedLock>()
                .eq("id", distributedLock.getId())
                .eq("expirationTime", distributedLock.getExpirationTime())
                .eq("deleteStatus", distributedLock.getDeleteStatus())
                .set("expirationTime", SystemClock.now() + expirationTime)
                .set("deleteStatus", DeleteStatus.NORMAL.getCode()));
    }
    
    
    /**
     * 方法描述：判断当前线程是否可以获取锁
     * @param distributedLock 锁信息
     * @return {@link Boolean} true，已经被其他线程加锁、 false 可以获取锁
     * @date 2023-02-14 13:54:15
     */
    private Boolean isLocked(DistributedLock distributedLock, LockEnum lockEnum) {
        // 如果线程锁不存在，证明可以获取锁
        if (distributedLock == null) {
            // 插入一条旧值，便于后续更新数据
            distributedLock = new DistributedLock(lockEnum.getCode(), 0L, DeleteStatus.DELETED.getCode(), LocalIpUtil.getInet6Address(), 0, "", 0);
            sqlDistributedLockService.save(distributedLock);
            return false;
        }
        // 判断当前锁是否已经过期  锁已删除|| 锁已过期
        if (Objects.equals(distributedLock.getDeleteStatus(), DeleteStatus.DELETED.getCode()) || SystemClock.now() > distributedLock.getExpirationTime()) {
            return false;
        }
        // 判断锁是否为当前线程锁拥有 IpV6相同 && 当前线程持有
        if (Objects.equals(distributedLock.getHostIpv6(), LocalIpUtil.getInet6Address()) && Objects.equals(distributedLock.getLockThreadId(), Thread.currentThread().getId())) {
            return false;
        }
        return true;
    }
}
