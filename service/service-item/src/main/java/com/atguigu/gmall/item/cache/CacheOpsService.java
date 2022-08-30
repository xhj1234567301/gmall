package com.atguigu.gmall.item.cache;

import com.atguigu.gmall.model.to.SkuDetailTo;

/**
 * @Author: LAZY
 * @Date: 2022/08/31/2022/8/31
 */
public interface CacheOpsService {
    <T>T getCacheData(String cacheKey, Class<T> clazz);

    Boolean bloomContains(Long skuId);

    boolean tryLock(Long skuId);

    void unLock(Long skuId);

    void saveData(String cacheKey, SkuDetailTo skuDetailTo);
}
