package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.feign.SkuDetailFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: LAZY
 * @Date: 2022/08/26/2022/8/26
 */
@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
//        Result<SkuDetailTo> skuDetail = skuDetailFeignClient.getSkuDetail(skuId);
//        return skuDetail.getData();

        SkuDetailTo skuDetailTo = new SkuDetailTo();
//        异步编排

        //1.sku基本信息
        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            Result<SkuInfo> skuInfoResult = skuDetailFeignClient.getSkuInfo(skuId);
            SkuInfo skuInfo = skuInfoResult.getData();
            skuDetailTo.setSkuInfo(skuInfo);
            return skuInfo;
        }, threadPoolExecutor);


        //2. 实时价格
        CompletableFuture<Void> priceResultFuture = skuInfoFuture.runAsync(() -> {
            Result<BigDecimal> priceResult = skuDetailFeignClient.getPrice(skuId);
            BigDecimal price = priceResult.getData();
            skuDetailTo.setPrice(price);
        }, threadPoolExecutor);


        //3.分类信息
        CompletableFuture<Void> categoryFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<CategoryViewTo> categoryViewToResult = skuDetailFeignClient.getCategoryViewTo(skuInfo.getCategory3Id());
            CategoryViewTo categoryViewTo = categoryViewToResult.getData();
            skuDetailTo.setCategoryView(categoryViewTo);
        }, threadPoolExecutor);


        //4.图片信息
        CompletableFuture<Void> skuImageListFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<List<SkuImage>> skuImageListResult = skuDetailFeignClient.getSkuImageList(skuId);
            List<SkuImage> skuImageList = skuImageListResult.getData();
            skuInfo.setSkuImageList(skuImageList);
        }, threadPoolExecutor);


        // 商品（sku）所属的SPU当时定义的所有销售属性名值组合
        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<List<SpuSaleAttr>> saleAttrAndValueMarkSkuResult = skuDetailFeignClient.getSaleAttrAndValueMarkSku(skuInfo.getSpuId(), skuId);
            List<SpuSaleAttr> saleAttrAndValueMarkSku = saleAttrAndValueMarkSkuResult.getData();
            skuDetailTo.setSpuSaleAttrList(saleAttrAndValueMarkSku);
        }, threadPoolExecutor);

        CompletableFuture.allOf(
                skuImageListFuture,
                priceResultFuture,
                saleAttrFuture,
                categoryFuture
        ).join();

        return skuDetailTo;
    }
}
