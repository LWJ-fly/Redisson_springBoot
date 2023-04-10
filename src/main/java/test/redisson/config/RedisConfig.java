package test.redisson.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class RedisConfig {
    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() throws IOException {
        Config config = Config.fromYAML(new File("src/main/resources/redisson.yaml"));
        return Redisson.create(config);
    }
}
