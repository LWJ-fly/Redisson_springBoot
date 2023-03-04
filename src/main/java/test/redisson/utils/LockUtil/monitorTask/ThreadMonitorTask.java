package test.redisson.utils.LockUtil.monitorTask;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.SystemClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import test.redisson.enums.DeleteStatus;
import test.redisson.utils.LocalIpUtil;
import test.redisson.utils.LockUtil.entity.DistributedLock;
import test.redisson.utils.LockUtil.sqlService.SqlDistributedLockService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 类描述：线程监控任务（看门狗机制）
 * @author 技匠
 * @date 2023-03-04 16:11:01
 * 版权所有 Copyright www.wenmeng.online
 */
@Component
public class ThreadMonitorTask {
    
    @Autowired
    private SqlDistributedLockService sqlDistributedLockService;
    // 获取锁的线程集合
    private static final CopyOnWriteArrayList<Thread> lockThreads = new CopyOnWriteArrayList<>();
    
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
                lock.setExpirationTime(now + 3 * 10 * 1000);
            } else {
                lock.setDeleteStatus(DeleteStatus.DELETED.getCode());
            }
        }
        sqlDistributedLockService.updateBatchById(distributedLocks);
    }
    
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
    
}
