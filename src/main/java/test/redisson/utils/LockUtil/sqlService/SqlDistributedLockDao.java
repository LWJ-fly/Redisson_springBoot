package test.redisson.utils.LockUtil.sqlService;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import test.redisson.utils.LockUtil.entity.DistributedLock;

/**
 * 类描述：分布式锁实现
 * @author 技匠
 * @date 2023-03-04 16:42:22
 * 版权所有 Copyright www.wenmeng.online
 */
@Mapper
public interface SqlDistributedLockDao extends BaseMapper<DistributedLock> {
}
