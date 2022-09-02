package com.atguigu.gmall.product.schedule;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 重建布隆过滤器
 * @Author: LAZY
 * @Date: 2022/09/01/2022/9/1
 */
@Service
public class RebuildBloomTask {

    @Autowired
    BloomDataQueryService bloomDataQueryService;

    @Autowired
    BloomOpsService bloomOpsService;

    /**
     * 重建布隆 定时执行
     */
    @Scheduled(cron = "0 0 3 ? * 3")
    public void rebuild(){
        System.out.println("111");
        bloomOpsService.rebuildBloom(SysRedisConst.BLOOM_SKUID,bloomDataQueryService);
    }

}
