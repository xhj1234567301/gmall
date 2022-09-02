package com.atguigu.starter.cache.service.impl;

import com.atguigu.starter.cache.constant.SysRedisConst;
import com.atguigu.starter.cache.service.CacheOpsService;
import com.atguigu.starter.cache.utils.Jsons;
import com.fasterxml.jackson.core.type.TypeReference;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

/**
 * @Author: LAZY
 * @Date: 2022/09/01/2022/9/1
 */
@Service
public class CacheOpsServiceImpl implements CacheOpsService {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;


    @Override
    public Object getCacheData(String cacheKey, Type type) {
        System.out.println("aop查缓存");
        String jsonStr = redisTemplate.opsForValue().get(cacheKey);

        //空值缓存
        if (SysRedisConst.NULL_VAL.equals(jsonStr)){
            return null;
        }
        //转成对象
        Object obj = Jsons.toObj(jsonStr, new TypeReference<Object>() {
            @Override
            public Type getType() {
                return type; //这个是方法的带泛型的返回值类型
            }
        });
        return obj;
    }

    @Override
    public boolean bloomContains(String bloomName, Object bloomValue) {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(bloomName);
        boolean contains = bloomFilter.contains(bloomValue);
        return contains;
    }

    @Override
    public boolean tryLock(String lockName) {
        RLock lock = redissonClient.getLock(lockName);
        return lock.tryLock();
    }

    @Override
    public void unlock(String lockName) {
        RLock lock = redissonClient.getLock(lockName);
        lock.unlock();
    }

    @Override
    public void saveDate(String cacheKey, Object result) {
        if (result == null){
            redisTemplate.opsForValue().set(cacheKey,
                    SysRedisConst.NULL_VAL,
                    SysRedisConst.NULL_VAL_TTL,
                    TimeUnit.SECONDS);
        }else {
            String str = Jsons.toStr(result);
            redisTemplate.opsForValue().set(
                    cacheKey,
                    str,
                    SysRedisConst.SKUDETAIL_TTL,
                    TimeUnit.SECONDS
            );
        }
    }
}
