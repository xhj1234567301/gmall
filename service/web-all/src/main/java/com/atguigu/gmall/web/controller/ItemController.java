package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.web.feign.SkuDetailFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: LAZY
 * @Date: 2022/08/26/2022/8/26
 */
@Controller
public class ItemController {
    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;
    /**
     * 商品详情页
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId") Long skuId,
                       Model model){
        Result<SkuDetailTo> skuDetail = skuDetailFeignClient.getSkuDetail(skuId);
        if (skuDetail.isOk()){
            SkuDetailTo data = skuDetail.getData();
            if(data == null || data == null){
                //说明远程没有查到商品
                return "seckill/fail";
            }
            SkuDetailTo skuDetailTo = skuDetail.getData();
            model.addAttribute("categoryView",skuDetailTo.getCategoryView());
            model.addAttribute("skuInfo",skuDetailTo.getSkuInfo());
            model.addAttribute("price",skuDetailTo.getPrice());
            model.addAttribute("spuSaleAttrList",skuDetailTo.getSpuSaleAttrList());//spu的销售属性列表
            model.addAttribute("valuesSkuJson",skuDetailTo.getValuesSkuJson());//json
        }
        return "item/index";
    }
}
