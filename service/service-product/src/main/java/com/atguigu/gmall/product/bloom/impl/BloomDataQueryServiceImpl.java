package com.atguigu.gmall.product.bloom.impl;

import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/09/01/2022/9/1
 */
@Service
public class BloomDataQueryServiceImpl implements BloomDataQueryService {
    @Autowired
    SkuInfoService skuInfoService;

    @Override
    public List queryData() {
        return skuInfoService.getSkuIdList();
    }
}
