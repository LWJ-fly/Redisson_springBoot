package test.redisson.utils.LockUtil.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import test.redisson.enums.DeleteStatus;
import test.redisson.utils.LocalIpUtil;

/**
 * 类描述：分布式锁实体类
 * @author 技匠
 * @date 2023-03-04 15:54:36
 * 版权所有 Copyright www.wenmeng.online
 */
@Data
@TableName("DistributedLock")
public class DistributedLock {
    private Integer id;
    
    /**
     * 过期时间
     */
    private Long expirationTime;
    
    /**
     * 删除状态（0 正常、1 删除）
     */
    private int deleteStatus = DeleteStatus.NORMAL.getCode();
    
    /**
     * 本机地址的IpV4
     */
    private String hostIpv4 = LocalIpUtil.getInet4Address();
    
    /**
     * 当前线程Id
     */
    private long lockThreadId;
    
    /**
     * 当前线程名称
     */
    private String lockThreadName;
    
    /**
     * 当前线程加锁次数
     */
    private Integer lockCount = 0;
    
    public DistributedLock() {
    }
    
    public DistributedLock(Integer id, Long expirationTime, int deleteStatus, long lockThreadId, String lockThreadName, Integer lockCount) {
        this.id = id;
        this.expirationTime = expirationTime;
        this.deleteStatus = deleteStatus;
        this.lockThreadId = lockThreadId;
        this.lockThreadName = lockThreadName;
        this.lockCount = lockCount;
    }
}
