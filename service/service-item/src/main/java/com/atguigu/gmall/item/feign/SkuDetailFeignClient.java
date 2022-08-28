package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/08/26/2022/8/26
 */
@RequestMapping("/api/inner/rpc/product")
@FeignClient("service-product")
public interface SkuDetailFeignClient {

    @GetMapping("/skudetail/{skuId}")
    Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId") Long skuId);

    /**
     * skuIndo
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/info/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable("skuId")Long skuId);

    /**
     * skuImageList
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/imageList/{skuId}")
    public Result<List<SkuImage>> getSkuImageList(@PathVariable("skuId")Long skuId);

    /**
     * 分类信息
     * @param c3Id
     * @return
     */
    @GetMapping("/skudetail/category/{c3Id}")
    public Result<CategoryViewTo> getCategoryViewTo(@PathVariable("c3Id")Long c3Id);

    /**
     * 查询实时价格
     * @param skuId
     * @return
     */
    @GetMapping("skudetail/price/{skuId}")
    public Result<BigDecimal> getPrice(@PathVariable("skuId")Long skuId);


    @GetMapping("skudetail/getSaleAttrAndValueMarkSku/{spuId}/{skuId}")
    public Result<List<SpuSaleAttr>> getSaleAttrAndValueMarkSku(@PathVariable("spuId") Long spuId,
                                                                @PathVariable("skuId") Long skuId);

    /**
     *查sku组合 valueJson
     * @param spuId
     * @return
     */
    @GetMapping("skudetail/valueJson/{spuId}")
    public Result<String> getSkuValueJson(@PathVariable("spuId")Long spuId);

}
