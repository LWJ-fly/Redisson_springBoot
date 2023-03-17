package test.redisson.mybatisUtil;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import test.redisson.utils.clazz.BeanUtil;
import test.redisson.utils.clazz.Getter;

/**
 * 类描述：mybatis更新条件封装
 * @author 8519
 * @date 2023-03-17 18:05:44
 */
public class MybatisUpdateWrapper<T> extends UpdateWrapper<T> {
    
    public MybatisUpdateWrapper() {
    }
    
    public MybatisUpdateWrapper(T entity) {
        super(entity);
    }
    
    public <R> MybatisUpdateWrapper<T> set(boolean condition, Getter<T, R> getter, Object val) {
        return (MybatisUpdateWrapper<T>) super.set(condition, BeanUtil.fieldName(getter), val);
    }
    
    public <R> MybatisUpdateWrapper<T> eq(Getter<T, R> getter, Object val) {
        return (MybatisUpdateWrapper<T>)super.eq(BeanUtil.fieldName(getter), val);
    }
    
    public <R> MybatisUpdateWrapper<T> set(Getter<T, R> getter, Object val) {
        return (MybatisUpdateWrapper<T>)super.set(BeanUtil.fieldName(getter), val);
    }
}
