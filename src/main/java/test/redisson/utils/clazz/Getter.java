package test.redisson.utils.clazz;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 类描述：承载类的getter方法
 * @author 8513
 * @date 2023-02-23 9:13:37
 * 版权所有 Copyright www.dahantc.com
 */
@FunctionalInterface
public interface Getter<T, R> extends Function<T, R>, Serializable {
}