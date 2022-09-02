package com.atguigu.starter.cache.service;

import java.lang.reflect.Type;

/**
 * @Author: LAZY
 * @Date: 2022/09/01/2022/9/1
 */
public interface CacheOpsService {

    /**
     * 从缓存中获取一个json并转为复杂对象
     * @param cacheKey
     * @param type
     * @return
     */
    Object getCacheData(String cacheKey,
                        Type type);

    /**
     * 判定指定布隆过滤器（bloomName） 是否 包含 指定值（bVal）
     * @param bloomName
     * @param bloomValue
     * @return
     */
    boolean bloomContains(String bloomName, Object bloomValue);

    /**
     * 加锁
     * @param lockName
     * @return
     */
    boolean tryLock(String lockName);

    void unlock(String lockName);

    /**
     * 把指定对象使用指定的key保存到redis
     * @param cacheKey
     * @param result
     */
    void saveDate(String cacheKey, Object result, Long ttl);
}
