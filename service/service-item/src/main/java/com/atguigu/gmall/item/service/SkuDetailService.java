package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.stereotype.Service;

/**
 * @Author: LAZY
 * @Date: 2022/08/26/2022/8/26
 */
public interface SkuDetailService {
    SkuDetailTo getSkuDetail(Long skuId);
}
