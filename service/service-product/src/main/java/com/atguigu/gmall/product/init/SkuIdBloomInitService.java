package com.atguigu.gmall.product.init;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 项目启动成功后首先 从数据库查出sku的id列表放到布隆过滤器中进行占位
 * @Author: LAZY
 * @Date: 2022/08/30/2022/8/30
 */
@Slf4j
@Service
public class SkuIdBloomInitService {

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    RedissonClient redissonClient;

    @PostConstruct //当前组件对象创建成功后执行
    public void initSkuBloom(){
        //1. 查出id列表
        log.info("布隆初始化正在进行....");
        List<Long> skuIdList = skuInfoMapper.getSkuIdList();

        //2.创建布隆过滤器
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        //3.初始化布隆过滤器
        boolean exists = bloomFilter.isExists();
        if (!exists){
            //尝试初始化 期望插入的数据量，误判率
            bloomFilter.tryInit(5000000,0.00001);
        }
        //4.id占位
        for (Long skuId : skuIdList) {
            bloomFilter.add(skuId);
        }

        log.info("布隆初始化完成....，总计添加了 {} 条数据",skuIdList.size());

    }

}
