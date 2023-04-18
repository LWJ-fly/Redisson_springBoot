package test.redisson.utils;

import com.alibaba.fastjson.JSON;
import org.redisson.api.RDeque;
import org.redisson.api.RExpirable;
import org.redisson.api.RedissonClient;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public enum RedisUtil {
    
    /**
     * 用户名
     */
    userName("用户名称");
    
    RedisUtil(String desc) {
    }
    
    /**
     * 获取配置文件，判断是否启用Redis
     */
    private final Boolean available = BeanFactory.getProperty("redis.available", Boolean.class);;
    /**
     * reids最大弹出数量
     */
    private final Long popRedisSize = BeanFactory.getProperty("redis.popRedisSize", Long.class);
    
    public boolean getAvailable() {
        return available;
    }
    
    private final static String SEPARATOR = "_";
    private final RedissonClient redissonClient = BeanFactory.getBean(RedissonClient.class);
    
    /**
     * 方法描述：保存到redis
     * @param key 保存键
     * @param val 保存值
     * @param expireType 设置过期时间
     * @date 2023-04-11 18:57:18
     */
    public void set(Object key, Object val, RedisUtils.ExpireType expireType) {
        if (Objects.equals(RedisUtils.ExpireType.NONE_EXPIRE.getExpireTime(), expireType.getExpireTime())) {
            redissonClient.getBucket(keyConvert(key)).set(val);
        } else {
            redissonClient.getBucket(keyConvert(key)).set(val, expireType.getExpireTime(), TimeUnit.SECONDS);
        }
    }
    
    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param expireType 过期时间枚举
     */
    public Boolean expire(Object key, RedisUtils.ExpireType expireType) {
        if (Objects.equals(RedisUtils.ExpireType.NONE_EXPIRE.getExpireTime(), expireType.getExpireTime())) {
            return redissonClient.getBucket(keyConvert(key)).clearExpire();
        } else {
            return expireType.expire(redissonClient.getBucket(keyConvert(key)));
        }
    }
    
    /**
     * 方法描述：获取redis指定键的值, 默认返回为String
     * @param key 保存键
     * @date 2023-04-11 18:57:18
     */
    public String get(Object key) {
        return get(key, String.class);
    }
    
    /**
     * 方法描述：获取redis指定键的值
     * @param key 保存键
     * @param clazz 指定返回类型
     * @date 2023-04-11 18:57:18
     */
    public <T> T get(Object key, Class<T> clazz) {
        Object val = redissonClient.getBucket(keyConvert(key)).get();
        return convert(val, clazz);
    }
    
    /**
     * 方法描述：从队列左端存值
     * @param key 键
     * @param expireType 过期时间
     * @param vals 值
     * @date 2023-04-12 13:38:55
     */
    public <T> int lPush(Object key, RedisUtils.ExpireType expireType, Collection<T> vals) {
        if (vals == null || vals.isEmpty()) {
            return 0;
        }
        return lPush(key, expireType, vals.toArray());
    }
    /**
     * 方法描述：从队列左端存值
     * @param key 键
     * @param expireType 过期时间
     * @param vals 值
     * @date 2023-04-12 13:38:55
     */
    public int lPush(Object key, RedisUtils.ExpireType expireType, Object ...vals) {
        RDeque<Object> deque = redissonClient.getDeque(keyConvert(key));
        expireType.expire(deque);
        return deque.addFirst(vals);
    }
    
    /**
     * 方法描述：从左侧获取指定数量的值
     * @param key 指定队列
     * @return 返回的值
     * @date 2023-04-12 14:18:43
     */
    public String lpop(Object key) {
        return lpop(key, String.class);
    }
    
    /**
     * 方法描述：从左侧获取指定数量的值
     * @param key 指定队列
     * @param clazz 指定返回类型
     * @return {@link T} 返回的值
     * @date 2023-04-12 14:18:43
     */
    public <T> T lpop(Object key, Class<T> clazz) {
        List<T> lpop = lpop(key, 1, clazz);
        if (lpop.isEmpty()) {
            return null;
        }
        return lpop.get(0);
    }
    
    /**
     * 方法描述：从左侧获取指定数量的值
     * @param key 指定队列
     * @param popNum 获取队列数据个数
     * @param clazz 指定返回类型
     * @return {@link List<T>} 返回指定数量的值
     * @date 2023-04-12 14:18:43
     */
    public <T> List<T> lpop(Object key, int popNum, Class<T> clazz) {
        if (popNum > popRedisSize) {
            popNum = Math.toIntExact(popRedisSize);
        }
        List<Object> objectList = redissonClient.getDeque(keyConvert(key)).pollFirst(popNum);
        return convert(objectList, clazz);
    }
    
    /**
     * 方法描述：从队列右端存值
     * @param key 键
     * @param expireType 过期时间
     * @param vals 值
     * @date 2023-04-12 13:38:55
     */
    public <T> int rPush(Object key, RedisUtils.ExpireType expireType, Collection<T> vals) {
        if (vals == null || vals.isEmpty()) {
            return 0;
        }
        return rPush(key, expireType, vals.toArray());
    }
    
    /**
     * 方法描述：从队列右端存值
     * @param key 键
     * @param expireType 过期时间
     * @param vals 值
     * @date 2023-04-12 13:38:55
     */
    public int rPush(Object key, RedisUtils.ExpireType expireType, Object ...vals) {
        if (vals == null) {
            return 0;
        }
        RDeque<Object> deque = redissonClient.getDeque(keyConvert(key));
        expireType.expire(deque);
        return deque.addLast(vals);
    }
    
    /**
     * 方法描述：从右侧获取指定数量的值
     * @param key 指定队列
     * @return 返回的值
     * @date 2023-04-12 14:18:43
     */
    public String rpop(Object key) {
        return rpop(key,  String.class);
    }
    
    /**
     * 方法描述：从右侧获取指定数量的值
     * @param key 指定队列
     * @param clazz 指定返回类型
     * @return {@link T} 返回的值
     * @date 2023-04-12 14:18:43
     */
    public <T> T rpop(Object key, Class<T> clazz) {
        List<T> rpop = rpop(key, 1, clazz);
        if (rpop.isEmpty()) {
            return null;
        }
        return rpop.get(0);
    }
    
    /**
     * 方法描述：从右侧获取指定数量的值
     * @param key 指定队列
     * @param popNum 获取队列数据个数
     * @param clazz 指定返回类型
     * @return {@link List<T>} 返回指定数量的值
     * @date 2023-04-12 14:18:43
     */
    public <T> List<T> rpop(Object key, int popNum, Class<T> clazz) {
        if (popNum > popRedisSize) {
            popNum = Math.toIntExact(popRedisSize);
        }
        List<Object> objectList = redissonClient.getDeque(keyConvert(key)).pollLast(popNum);
        return convert(objectList, clazz);
    }
    
    
    public enum ExpireType {
        /**
         * 保存一天   60 * 60 * 24 = 86400 秒
         */
        DEFAULT_EXPIRE(86400, "保存一天"),
        /**
         * 保存两个小时 60 * 60 * 2 = 7200 秒
         */
        TWO_HOURS_EXPIRE(7200, "保存两个小时"),
        /**
         * 保存一个小时 60 * 60 = 3600 秒
         */
        HOURS_EXPIRE(3600, "保存一个小时"),
        /**
         * 保存30分钟 60 * 30 = 1800 秒
         */
        HALF_AN_HOUR(1800, "保存30分钟"),
        /**
         * 保存15分钟 60 * 15 = 900 秒
         */
        TWO_MINUTES_EXPIRE(900, "保存15分钟"),
        /**
         * 请求超时， 保存5分钟 60 * 5 = 300 秒
         */
        QUERY_EXPIRE(300, "请求超时， 保存5分钟"),
        /**
         * 保存15秒
         */
        HALF_MINUTE_EXPIRE(15, "保存15秒"),
        
        /**
         * 已经过期
         */
        EXPIRED(-1, "已经过期"),
        
        /**
         * 动态设定时间
         */
        DYNAMIC_EXPIRE(86400, "动态设定时间， 默认一天"),
        
        /**
         * 永不过期
         */
        NONE_EXPIRE(-99, "永不过期");
        
        /**
         * 过期时间
         */
        private Integer expireTime;
        /**
         * 描述
         */
        private final String desc;
        
        ExpireType(Integer expireTime, String desc) {
            this.expireTime = expireTime;
            this.desc = desc;
        }
        
        public RedisUtil.ExpireType setExpireTime(Long expireTime) {
            Assert.notNull(expireTime, "the expiration time cannot be empty【过期时间不能为空】");
            return setExpireTime(Integer.parseInt(String.valueOf(expireTime)));
        }
        public RedisUtil.ExpireType setExpireTime(Integer expireTime) {
            Assert.notNull(expireTime, "the expiration time cannot be empty【过期时间不能为空】");
            if (this != DYNAMIC_EXPIRE) {
                Assert.notNull(expireTime, "Non-specified dynamic time enumeration. Changing the save time is not supported【非指定动态时间枚举，不支持更改保存时间】");
            }
            if (expireTime < 0) {
                Assert.notNull(expireTime, "the dynamic time cannot be less than 0【指定动态时间不能小于0】");
            }
            this.expireTime = expireTime;
            return this;
        }
        
        public Integer getExpireTime() {
            return expireTime;
        }
        
        public String getDesc() {
            return desc;
        }
        
        public Boolean expire(RExpirable expirable) {
            if (this == RedisUtil.ExpireType.NONE_EXPIRE) {
                return expirable.clearExpire();
            }
            return expirable.expire(Duration.ofSeconds(this.getExpireTime()));
        }
    }
    
    private String keyConvert(Object key) {
        verify();
        Assert.notNull(key, "redis key cannot be empty【Redis key 不能为空】");
        if (key instanceof String) {
            return this.name() + SEPARATOR + key;
        }
        return this.name() + SEPARATOR + JSON.toJSONString(key);
    } /**
     * 方法描述：内部数据转换为指定对象
     * @param val 源对象
     * @param clazz 指定类
     * @return {@link T} 返回指定对象类型
     * @date 2023-04-12 14:13:16
     */
    private <T> T convert(Object val, Class<T> clazz) {
        if (val == null) {
            return null;
        }
        if (val.getClass() == clazz) {
            return (T)val;
        }
        if (clazz == String.class) {
            return (T) JSON.toJSONString(val);
        }
        return JSON.parseObject(JSON.toJSONString(val), clazz);
    }
    
    /**
     * 方法描述：内部数据转换为指定对象
     * @param vals 源对象
     * @param clazz 指定类
     * @return {@link T} 返回指定对象类型
     * @date 2023-04-12 14:13:16
     */
    private <T> List<T> convert(List<Object> vals, Class<T> clazz) {
        if (vals == null || vals.isEmpty()) {
            return new ArrayList<>();
        }
        if (vals.get(0).getClass() == clazz) {
            return (List<T>) vals;
        }
        List<T> tList = new ArrayList<>();
        for (Object val : vals) {
            tList.add(convert(val, clazz));
        }
        return tList;
    }
    private void verify() {
        Assert.isTrue(getAvailable(), "redis unavailable operate failed【Redis不可用， 操作失败】");
    }
}
