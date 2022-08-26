package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.service.SkuInfoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: LAZY
 * @Date: 2022/08/26/2022/8/26
 */
@Api(tags = "sku查询RPC接口")
@RestController
@RequestMapping("/api/inner/rpc/product")
public class SkuDetailApiController {

    @Autowired
    SkuInfoService skuInfoService;

    @GetMapping("/skudetail/{skuId}")
    public Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId") Long skuId){
        SkuDetailTo skuDetailTo = skuInfoService.getSkuDetail(skuId);
        return  Result.ok(skuDetailTo);
    }
}
