package test.redisson.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import test.redisson.config.RedissonConfig;

/**
 * 类描述：请求
 * @author 技匠
 * @date 2023-02-16 17:16:19
 * 版权所有 Copyright www.wenmeng.online
 */
@RestController
public class web {
    
    @Autowired
    RedissonConfig redissonConfig;
    
    @GetMapping("test")
    public String getYml() {
        return redissonConfig.getPort();
    }
}
