package com.atguigu.gmall.web.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: LAZY
 * @Date: 2022/08/26/2022/8/26
 */
@RequestMapping("/api/inner/rpc/item")
@FeignClient("service-item")
public interface SkuDetailFeignClient {

    @RequestMapping("/skudetail/{skuId}")
    Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId")Long skuId);
}
