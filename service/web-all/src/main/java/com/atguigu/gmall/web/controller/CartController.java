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
}
