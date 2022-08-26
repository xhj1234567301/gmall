package com.atguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.to.SkuDetailTo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: LAZY
 * @Date: 2022/08/26/2022/8/26
 */
@Api(tags = "商品详情")
@RestController
@RequestMapping("/api/inner/rpc/item")
public class SkuDetailApiController {
    @Autowired
    SkuDetailService skuDetailService;

    @RequestMapping("/skudetail/{skuId}")
    public Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId")Long skuId){
        //商品的详情
        SkuDetailTo skuDetailTo = skuDetailService.getSkuDetail(skuId);
        return Result.ok(skuDetailTo);
    }
}
