package test.redisson.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 类描述：Redisson配置类信息
 * @author 技匠
 * @date 2023-02-16 17:11:56
 * 版权所有 Copyright www.wenmeng.online
 */
@Configuration
@ConfigurationProperties(prefix = "redisson")
public class RedissonConfig {
    private String port;
    
    public String getPort() {
        return port;
    }
    
    public void setPort(String port) {
        this.port = port;
    }
}
