package com.atguigu.gmall.item.cache.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.cache.CacheOpsService;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author: LAZY
 * @Date: 2022/08/31/2022/8/31
 */
@Service
public class CacheOpsServiceImpl implements CacheOpsService {
    @Autowired
    StringRedisTemplate redisTemplate;
    
    @Autowired
    RedissonClient redissonClient;

    @Override
    public <T> T getCacheData(String cacheKey, Class<T> clazz) {
        String res = redisTemplate.opsForValue().get(cacheKey);
        T t = Jsons.toObj(res, clazz);
        return t;
    }

    @Override
    public Boolean bloomContains(Long skuId) {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        boolean contains = bloomFilter.contains(skuId);
        return contains;
    }

    @Override
    public boolean tryLock(Long skuId) {
        //锁的key
        String lockKey = SysRedisConst.LOCK_SKU_DETAIL+skuId;
        RLock lock = redissonClient.getLock(lockKey);
        //尝试加锁
        boolean b = lock.tryLock();
        return b;
    }

    @Override
    public void unLock(Long skuId) {
        //锁的key
        String lockKey = SysRedisConst.LOCK_SKU_DETAIL+skuId;
        RLock lock = redissonClient.getLock(lockKey);
        //解锁
        lock.unlock();
    }

    @Override
    public void saveData(String cacheKey, SkuDetailTo skuDetailTo) {
        if (skuDetailTo == null){
            //空值缓存
            redisTemplate.opsForValue().set(cacheKey,
                    SysRedisConst.NULL_VAL,
                    SysRedisConst.NULL_VAL_TTL,
                    TimeUnit.SECONDS);
        }else {
            //缓存到redis
            redisTemplate.opsForValue().set(cacheKey,
                    Jsons.toStr(skuDetailTo),
                    SysRedisConst.SKUDETAIL_TTL,
                    TimeUnit.DAYS);
        }
    }
}
