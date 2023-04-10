package test.redisson.mybatisUtil;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import test.redisson.utils.clazz.BeanUtil;
import test.redisson.utils.clazz.Getter;

import java.util.Collection;

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
    
    public <R> MybatisUpdateWrapper<T> le(Getter<T, R> getter, Object val) {
        return (MybatisUpdateWrapper<T>)super.le(BeanUtil.fieldName(getter),  val);
    }
    
    public <R> MybatisUpdateWrapper<T> eq(boolean condition, Getter<T, R> getter, Object val) {
        return (MybatisUpdateWrapper<T>)super.eq(condition, BeanUtil.fieldName(getter), val);
    }
    
    public <R> MybatisUpdateWrapper<T> ne(boolean condition, Getter<T, R> getter, Object val) {
        return (MybatisUpdateWrapper<T>)super.ne(condition, BeanUtil.fieldName(getter), val);
    }
    
    
    public <R> MybatisUpdateWrapper<T> gt(boolean condition, Getter<T, R> getter, Object val) {
        return (MybatisUpdateWrapper<T>)super.gt(condition, BeanUtil.fieldName(getter), val);
    }
    
    public <R> MybatisUpdateWrapper<T> ge(boolean condition, Getter<T, R> getter, Object val) {
        return (MybatisUpdateWrapper<T>)super.ge(condition, BeanUtil.fieldName(getter), val);
    }
    
    public <R> MybatisUpdateWrapper<T> ge( Getter<T, R> getter, Object val) {
        return (MybatisUpdateWrapper<T>)super.ge(BeanUtil.fieldName(getter), val);
    }
    
    
    public <R> MybatisUpdateWrapper<T> lt(boolean condition, Getter<T, R> getter, Object val) {
        return (MybatisUpdateWrapper<T>)super.lt(condition, BeanUtil.fieldName(getter), val);
    }
    
    
    public <R> MybatisUpdateWrapper<T> le(boolean condition, Getter<T, R> getter, Object val) {
        return (MybatisUpdateWrapper<T>)super.le(condition, BeanUtil.fieldName(getter), val);
    }
    
    
    public <R> MybatisUpdateWrapper<T> like(boolean condition, Getter<T, R> getter, Object val) {
        return (MybatisUpdateWrapper<T>)super.like(condition, BeanUtil.fieldName(getter), val);
    }
    
    
    public <R> MybatisUpdateWrapper<T> notLike(boolean condition, Getter<T, R> getter, Object val) {
        return (MybatisUpdateWrapper<T>)super.notLike(condition, BeanUtil.fieldName(getter), val);
    }
    
    
    public <R> MybatisUpdateWrapper<T> likeLeft(boolean condition, Getter<T, R> getter, Object val) {
        return (MybatisUpdateWrapper<T>)super.likeLeft(condition, BeanUtil.fieldName(getter), val);
    }
    
    
    public <R> MybatisUpdateWrapper<T> likeRight(boolean condition, Getter<T, R> getter, Object val) {
        return (MybatisUpdateWrapper<T>)super.likeRight(condition, BeanUtil.fieldName(getter), val);
    }
    
    
    public <R> MybatisUpdateWrapper<T> between(boolean condition, Getter<T, R> getter, Object val1, Object val2) {
        return (MybatisUpdateWrapper<T>)super.between(condition, BeanUtil.fieldName(getter), val1, val2);
    }
    
    
    public <R> MybatisUpdateWrapper<T> notBetween(boolean condition, Getter<T, R> getter, Object val1, Object val2) {
        return (MybatisUpdateWrapper<T>)super.notBetween(condition, BeanUtil.fieldName(getter), val1, val2);
    }
    
    
    public <R> MybatisUpdateWrapper<T> isNull(boolean condition, String column) {
        return (MybatisUpdateWrapper<T>)super.isNull(condition, column);
    }
    
    
    public <R> MybatisUpdateWrapper<T> isNotNull(boolean condition, String column) {
        return (MybatisUpdateWrapper<T>)super.isNotNull(condition, column);
    }
    
    
    public <R> MybatisUpdateWrapper<T> in(boolean condition, Getter<T, R> getter, Collection<?> coll) {
        return (MybatisUpdateWrapper<T>)super.in(condition, BeanUtil.fieldName(getter), coll);
    }
    
    
    public <R> MybatisUpdateWrapper<T> notIn(boolean condition, Getter<T, R> getter, Collection<?> coll) {
        return (MybatisUpdateWrapper<T>)super.notIn(condition, BeanUtil.fieldName(getter), coll);
    }
    
    
    public <R> MybatisUpdateWrapper<T> inSql(boolean condition, Getter<T, R> getter, String inValue) {
        return (MybatisUpdateWrapper<T>)super.inSql(condition, BeanUtil.fieldName(getter), inValue);
    }
    
    
    public <R> MybatisUpdateWrapper<T> notInSql(boolean condition, Getter<T, R> getter, String inValue) {
        return (MybatisUpdateWrapper<T>)super.notInSql(condition, BeanUtil.fieldName(getter), inValue);
    }
    
    
    public <R> MybatisUpdateWrapper<T> groupBy(boolean condition, String... columns) {
        return (MybatisUpdateWrapper<T>)super.groupBy(condition, columns);
    }
    
    
    public <R> MybatisUpdateWrapper<T> orderBy(boolean condition, boolean isAsc, String... columns) {
        return (MybatisUpdateWrapper<T>)super.orderBy(condition, isAsc, columns);
    }
}
