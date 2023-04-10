package test.redisson.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import test.redisson.utils.LockUtil.enums.LockEnum;
import test.redisson.utils.LockUtil.service.DistributedLockService;
import test.redisson.utils.LockUtil.service.impl.DistributedLockServiceImpl;

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
