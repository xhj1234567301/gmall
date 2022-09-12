package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: LAZY
 * @Date: 2022/09/07/2022/9/7
 */
@Controller
public class CartController {

    @Autowired
    CartFeignClient cartFeignClient;

    @GetMapping("addCart.html")
    public String addCartHtml(@RequestParam("skuId") Long skuId,
                              @RequestParam("skuNum") Integer skuNum,
                              Model model){
        Result<SkuInfo> skuInfoResult = cartFeignClient.addToCart(skuId, skuNum);
        model.addAttribute("skuIndo",skuInfoResult.getData());
        model.addAttribute("skuNum",skuNum);

        return "cart/addCart";
    }

    /**
     * 购物车列表页
     * @return
     */
    @GetMapping("/cart.html")
    public String cartHtml(){
        return "cart/index";
    }

    /**
     * 删除购物车中选中商品
     * @return
     */
    @GetMapping("/cart/deleteChecked")
    public String deleteChecked(){

        /**
         * redirect: 重定向
         * forward: 转发
         */
        cartFeignClient.deleteChecked();
        return "redirect:http://cart.gmall.com/cart.html";
    }
}
