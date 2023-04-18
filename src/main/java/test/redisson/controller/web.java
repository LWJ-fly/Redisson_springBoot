package test.redisson.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import test.redisson.utils.LockUtil.enums.LockEnum;
import test.redisson.utils.LockUtil.service.impl.DistributedLockServiceImpl;
import test.redisson.utils.RedisUtil;
import test.redisson.utils.RedisUtils;

/**
 * 类描述：请求
 * @author 技匠
 * @date 2023-02-16 17:16:19
 * 版权所有 Copyright www.wenmeng.online
 */
@RestController
public class web {
    
    private static boolean unLock = false;
    @Autowired
    @Qualifier("distributedLockServiceImpl")
    DistributedLockServiceImpl distributedLockService;
    
    @Autowired
    RedisUtils redisUtil;
    
    @GetMapping("test")
    public String test() throws Exception {
        redisUtil.set(123, RandomStringUtils.random(6), RedisUtils.ExpireType.QUERY_EXPIRE);
        return RedisUtil.userName.get(123, String.class);
    }
    
    @GetMapping("lock")
    public String lock() throws InterruptedException {
        unLock = false;
        distributedLockService.lock(LockEnum.EXPORT_TASK);
        while (true) {
            Thread.sleep(1000);
            if (unLock) {
                break;
            }
        }
        distributedLockService.unLock(LockEnum.EXPORT_TASK);
        return "success";
    }
    
    @GetMapping("unLock")
    public Boolean unLock() {
        unLock = true;
        return unLock;
    }
    
    @GetMapping("redis")
    public String redis() {
        String key = RandomStringUtils.randomAlphanumeric(5);
        redisUtil.set(key, RandomStringUtils.random(8), RedisUtils.ExpireType.QUERY_EXPIRE);
        return key;
    }
    
    @GetMapping("autoLock")
    public Boolean autoLock() {
        return distributedLockService.automaticRenewallock(LockEnum.EXPORT_TASK, () -> {
            Boolean flag = false;
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                }
                flag = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return flag;
        });
    }
    
    
}
