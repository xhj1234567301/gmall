package com.atguigu.gmall.product.bloom;

/**
 * 重建布隆
 * @Author: LAZY
 * @Date: 2022/09/01/2022/9/1
 */
public interface BloomOpsService {
    /**
     * 重建指定布隆过滤器
     * @param bloomName
     */
    void rebuildBloom(String bloomName,BloomDataQueryService dataQueryService);
}
