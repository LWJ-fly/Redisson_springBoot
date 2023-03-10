package test.redisson.utils.clazz;

import lombok.extern.slf4j.Slf4j;
import test.redisson.utils.LockUtil.entity.DistributedLock;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类描述：Java类处理工具类
 * @author 8513
 * @date 2023-02-23 9:15:00
 * 版权所有 Copyright www.dahantc.com
 */
@Slf4j
public class BeanUtil {

    public static final String GET_HEADER = "get";
    public static final String IS_HEADER = "is";

    private static final Map<Class<?>, SerializedLambda> CLASS_LAMBDA_CACHE = new ConcurrentHashMap<>();

    /**
     * 方法描述：通过getter方法获取属性名
     * @param getter {@link Getter} getter方法，建议使用方法引用
     * @return {@link String} 属性名
     * @date 2023-02-23 09:28:32
     */
    public static <T, R> String fieldName(Getter<T, R> getter) {
        SerializedLambda lambda = CLASS_LAMBDA_CACHE.get(getter.getClass());
        if (lambda == null) {
            try {
                // 提取SerializedLambda并缓存
                Method method = getter.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                lambda = (SerializedLambda) method.invoke(getter);
                CLASS_LAMBDA_CACHE.put(getter.getClass(), lambda);
            } catch (Exception e) {
                log.error("方法获取属性名异常", e);
            }
        }
        String methodName = lambda.getImplMethodName();
        if (methodName.startsWith(GET_HEADER)) {
            methodName = methodName.substring(3);
        } else if (methodName.startsWith(IS_HEADER)) {
            methodName = methodName.substring(2);
        } else {
            throw new IllegalArgumentException("无效的getter方法：" + methodName);
        }
        return methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
    }
    
    public static void main(String[] args) {
        System.out.println(fieldName(DistributedLock::getDeleteStatus));
    }
}