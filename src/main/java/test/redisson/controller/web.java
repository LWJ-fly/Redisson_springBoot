package test.redisson.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import test.redisson.utils.LockUtil.enums.LockEnum;
import test.redisson.utils.LockUtil.service.impl.DistributedLockserviceImpl;

/**
 * 类描述：请求
 * @author 技匠
 * @date 2023-02-16 17:16:19
 * 版权所有 Copyright www.wenmeng.online
 */
@RestController
public class web {
    
    @Autowired
    DistributedLockserviceImpl distributedLockService;
    
    private static boolean unLock = false;
    
    @GetMapping("lock")
    public String getYml() throws InterruptedException {
        unLock = false;
        Boolean lock = distributedLockService.tryLock(LockEnum.EXPORT_TASK);
        if (!lock) {
            return "false";
        }
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
    public Boolean lock() {
        unLock = true;
        return unLock;
    }
}
