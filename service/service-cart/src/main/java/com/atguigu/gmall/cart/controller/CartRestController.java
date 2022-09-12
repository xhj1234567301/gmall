package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车处理前端 ajax请求等
 * @Author: LAZY
 * @Date: 2022/09/07/2022/9/7
 */
@RequestMapping("/api/cart")
@RestController
public class CartRestController {

    @Autowired
    CartService cartService;

    /**
     * 查询购物车列表
     * @return
     */
    @GetMapping("/cartList")
    public Result cartList(){
        //购物车的键
        String cartKey = cartService.determinCartKey();
        //尝试合并购物车内容
        cartService.mergeUserAndTempCart();
        //查询购物车内容
        List<CartInfo> cartInfoList = cartService.getCartList(cartKey);
        return  Result.ok(cartInfoList);

    }

    /**
     * 修改商品数量
     * @param skuId
     * @param num
     * @return
     */
    @PostMapping("addToCart/{skuId}/{num}")
    public Result updateItemNum(@PathVariable("skuId") Long skuId,
                                @PathVariable("num") Integer num){
        //购物车的键
        String cartKey = cartService.determinCartKey();
        cartService.updateItemNum(skuId,num,cartKey);
        return Result.ok();
    }

    /**
     * 修改商品选中
     * @param skuId
     * @param status
     * @return
     */
    @GetMapping("checkCart/{skuId}/{status}")
    public Result updateCheckStatus(@PathVariable("skuId") Long skuId,
                                    @PathVariable("status") Integer status){
        //购物车的键
        String cartKey = cartService.determinCartKey();
        cartService.updateCheckStatus(skuId,status,cartKey);
        return Result.ok();
    }

    @DeleteMapping("deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId){
        //购物车的键
        String cartKey = cartService.determinCartKey();
        cartService.deleteCartItem(skuId,cartKey);
        return Result.ok();
    }
}
