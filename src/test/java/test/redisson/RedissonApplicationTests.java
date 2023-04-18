package test.redisson;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import test.redisson.utils.RedisUtil;
import test.redisson.utils.RedisUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RedissonApplicationTests {
    private final static String redisHost = "redis://172.18.1.102:6379";
    private final static Integer redisDatabase = 0;
    private final static String redisPWD = "redis.com";
    private static Config config;
    
    public static RedissonClient getClient() {
        return Redisson.create(createConfig());
    }
    
    public static Config createConfig() {
        if (config == null) {
            config = new Config();
            // 设置编码，String字符不进行转码，便于查看使用
//            config.setCodec(new StringCodec());
//            config.setCodec(new SerializationCodec());
            config.useSingleServer()
                    .setAddress(redisHost)
                    .setDatabase(redisDatabase)
                    .setPassword(redisPWD);
        }
        return config;
    }
    
    /* @param nThreads the number of threads in the pool
     * @return the newly created thread pool
     * @throws IllegalArgumentException if {@code nThreads <= 0}
     */
    public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }
    
    @Test
    public void contextLoads() throws IOException {
        Config config = new Config();
        try {
            config
                    .setCodec(new StringCodec())
                    .useSingleServer()
                    .setAddress(redisHost)
                    .setDatabase(redisDatabase)
                    .setPassword(redisPWD);
        } catch (Exception e) {
        }
        RedissonClient redisson = Redisson.create(config);
//        Object andSet = redisson.getBucket("9").getAndSet("12345678914", 10, TimeUnit.MINUTES);
//        System.out.println(andSet);
//        RAtomicDouble atomicDouble = redisson.getAtomicDouble("6");
//        double get = atomicDouble.getAndAdd(123);
//        System.out.println(get);
//        List<String> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            list.add(""+ i);
//        }
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.put(i, RandomStringUtils.randomNumeric(5));
        }
        String jsonString = JSON.toJSONString(map);
        System.out.println(jsonString);
        Map parse = JSON.parseObject(jsonString, Map.class);
        System.out.println(parse);
        System.out.println(redisson.getBucket("987654").getAndSet(jsonString, 10, TimeUnit.MINUTES));
        System.out.println(Objects.equals(parse.get(0), map.get(0)));
    }
    
    /**
     * 方法描述：添加若干数字到redis
     * @date 2023-02-17 13:53:51
     */
    @Test
    public void addToRedis() {
        RedissonClient client = getClient();
        for (int i = 0; i < 50; i++) {
            System.out.println(client.getBucket(RandomStringUtils.randomNumeric(7)).getAndSet(RandomStringUtils.random(9)));
        }
    }
    
    /**
     * 方法描述：字符串模糊查询
     */
    @Autowired
    RedisUtils redisUtil;
    
    @Test
    public void getString() {
        redisUtil.set(123, 1234, RedisUtils.ExpireType.TWO_MINUTES_EXPIRE);
        String name = RedisUtil.userName.get(123, String.class);
        System.out.println(name);
    }
    
    /**
     * 方法描述：删除所有Key
     */
    @Test
    public void delAll() {
        RedissonClient client = getClient();
        System.out.println(client.getKeys().deleteByPattern("*"));
    }
    
    @Test
    public void test1() throws Exception {
        ExecutorService threadPool = newFixedThreadPool(20);
        CopyOnWriteArrayList<Thread> threads = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            createThread(threadPool, threads, finalI);
        }
        Map<Long, String> threadIds = new HashMap<>();
        for (Thread thread : threads) {
            threadIds.put(thread.getId(), thread.getName());
        }
        Thread.sleep(10000);
        CopyOnWriteArrayList<Thread> threadsTemp = new CopyOnWriteArrayList<>();
        for (int i = 0; i < threads.size() * 100; i++) {
            Thread.sleep(1000);
            for (int j = 0; j < 10; j++) {
                createThread(threadPool, threadsTemp, 1);
            }
            for (Thread thread1 : threadsTemp) {
                if (threadIds.containsKey(thread1.getId()) && Objects.equals(threadIds.get(thread1.getId()), thread1.getName())) {
                    System.out.println("线程Id重复 = " + thread1.getId());
                }
            }
            for (Thread thread : threads) {
                System.out.println(thread.getId() + " --status = " + thread.isAlive());
            }
            System.out.println("\r\n\r\n\r\n\r\n");
        }
    }
    
    private void createThread(ExecutorService threadPool, CopyOnWriteArrayList<Thread> threads, int liveTimes) {
        threadPool.execute(() -> {
            Thread currentThread = Thread.currentThread();
            currentThread.setName(RandomStringUtils.randomNumeric(5));
            if (threads != null) {
                threads.add(currentThread);
            }
            System.out.println(currentThread.getId() + " --start");
            for (int j = 0; j < liveTimes; j++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
            System.out.println(currentThread.getId() + " --runEnd");
        });
        
    }
    
}
