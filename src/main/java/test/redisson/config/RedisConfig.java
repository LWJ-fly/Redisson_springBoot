package test.redisson.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class RedisConfig {
	private final static String redisHost = "redis://172.18.1.102:6379";
	private final static Integer redisDatabase = 0;
	private final static String redisPWD = "redis.com";
	@Bean(destroyMethod = "shutdown")
	RedissonClient redisson() throws IOException {
		Config config = Config.fromYAML(new File("src/main/resources/redisson.yaml"));
//		try {
//			config
//					.setCodec(new StringCodec())
//					.useSingleServer()
//					.setAddress(redisHost)
//					.setDatabase(redisDatabase)
//					.setPassword(redisPWD);
//		} catch (Exception e) {
//		}
		return Redisson.create(config);
	}
}
