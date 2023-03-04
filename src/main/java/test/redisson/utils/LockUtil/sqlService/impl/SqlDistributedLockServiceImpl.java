package test.redisson.utils.LockUtil.sqlService.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import test.redisson.utils.LockUtil.entity.DistributedLock;
import test.redisson.utils.LockUtil.sqlService.SqlDistributedLockDao;
import test.redisson.utils.LockUtil.sqlService.SqlDistributedLockService;

/**
 * 类描述：分布式锁实现
 * @author 技匠
 * @date 2023-03-04 16:40:11
 * 版权所有 Copyright www.wenmeng.online
 */
@Service
public class SqlDistributedLockServiceImpl extends ServiceImpl<SqlDistributedLockDao, DistributedLock> implements SqlDistributedLockService {
}
