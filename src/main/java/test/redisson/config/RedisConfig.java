package test.redisson.config;

import org.springframework.context.annotation.Configuration;
 
@Configuration
public class RedisConfig {
	private final static String redisHost = "redis://172.18.1.102:6379";
	private final static Integer redisDatabase = 0;
	private final static String redisPWD = "redis.com";
//	@Bean(destroyMethod = "shutdown")
//	RedissonClient redisson() throws IOException {
//		Config config = new Config();
//		try {
//			config
//					.setCodec(new StringCodec())
//					.useSingleServer()
//					.setAddress(redisHost)
//					.setDatabase(redisDatabase)
//					.setPassword(redisPWD);
//		} catch (Exception e) {
//		}
//		return Redisson.create(config);
//	}
}
