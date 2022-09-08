package com.atguigu.gmall.feign.cart;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: LAZY
 * @Date: 2022/09/07/2022/9/7
 */
@RequestMapping("api/inner/rpc/cart")
@FeignClient("service-cart")
public interface CartFeignClient {

    /**
     * 添加到购物车
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("addToCart")
    Result<SkuInfo> addToCart(@RequestParam("skuId") Long skuId,
                                     @RequestParam("num") Integer num);
//  @RequestHeader(value = SysRedisConst.USERID_HEADER,required = false) String userId 隐藏

}
