package com.atguigu.gmall.product.bloom.impl;

import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import com.atguigu.gmall.product.service.SkuInfoService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 重建布隆
 * @Author: LAZY
 * @Date: 2022/09/01/2022/9/1
 */
@Service
public class BloomOpsServiceImpl implements BloomOpsService {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    SkuInfoService skuInfoService;

    /**
     * 重建布隆过滤器
     * @param bloomName 过滤器名称
     * @param dataQueryService
     */
    @Override
    public void rebuildBloom(String bloomName, BloomDataQueryService dataQueryService) {
        RBloomFilter<Object> oldBloomFilter = redissonClient.getBloomFilter(bloomName);

        //创建新的布隆过滤器
        String newBloomName = bloomName +"_new";
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(newBloomName);
        //2、查询id集合
        List list = dataQueryService.queryData();
        //3、初始化新的布隆
        bloomFilter.tryInit(5000000,0.00001);

        for (Object skuId : list) {
            bloomFilter.add(skuId);
        }
        //4、交换
        oldBloomFilter.rename("temp_bloomFilter");
        bloomFilter.rename(bloomName);
        //5、删除
        oldBloomFilter.deleteAsync();
        redissonClient.getBloomFilter("temp_bloomFilter").deleteAsync();

    }
}
