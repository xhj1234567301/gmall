package com.atguigu.gmall.cart.api;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 购物车内部调用模块
 * @Author: LAZY
 * @Date: 2022/09/07/2022/9/7
 */
@RequestMapping("api/inner/rpc/cart")
@RestController
public class CartApiController {

    @Autowired
    CartService cartService;

    @GetMapping("addToCart")
    public Result<SkuInfo> addToCart(@RequestParam("skuId") Long skuId,
                                     @RequestParam("num") Integer num){
        SkuInfo skuInfo = cartService.addToCart(skuId,num);
        return Result.ok(skuInfo);
    }


    /**
     * 删除购物车中选中的商品
     * @return
     */
    @GetMapping("/deleteChecked")
    public Result deleteChecked(){
        String cartKey = cartService.determinCartKey();
        cartService.deleteChecked(cartKey);
        return Result.ok();
    }

}
