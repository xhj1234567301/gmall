package com.atguigu.gmall.cart.api;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.web.bind.annotation.*;

/**
 * 购物车内部调用模块
 * @Author: LAZY
 * @Date: 2022/09/07/2022/9/7
 */
@RequestMapping("api/inner/rpc/cart")
@RestController
public class CartApiController {

    @GetMapping("addToCart")
    public Result<SkuInfo> addToCart(@RequestParam("skuId") Long skuId,
                                     @RequestParam("num") Integer num,
                                     @RequestHeader(value = SysRedisConst.USERID_HEADER,required = false) String userId){
        System.out.println("用户Id = " + userId);
        return Result.ok();
    }

}
