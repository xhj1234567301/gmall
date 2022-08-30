package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.feign.SkuDetailFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 添加redis缓存 null（x）值缓存
     * @param skuId
     * @return
     */
    @Override
    public SkuDetailTo getSkuDetail(Long skuId){
        String jsonStr = redisTemplate.opsForValue().get("sku:info:" + skuId);
        //redis中没有 查到x 说明之前已经查过存x 防止再次回源
        if ("x".equals(jsonStr)) {
            return null;
        }
        if(StringUtils.isEmpty(jsonStr)) {
            //从数据库中查
            SkuDetailTo skuDetailRPC = getSkuDetailRPC(skuId);
            //防止随机值穿透缓存
            if (skuDetailRPC != null){
                //放入缓存
                redisTemplate.opsForValue().set("sku:info:"+ skuId,Jsons.toStr(skuDetailRPC),7, TimeUnit.DAYS);
            }else {
                redisTemplate.opsForValue().set("sku:info:"+ skuId,"x",30,TimeUnit.MINUTES);
            }
            //保存到redis
            return skuDetailRPC;
        }else {
            return Jsons.toObj(jsonStr,SkuDetailTo.class);
        }

    }

    /**
     * 无法解决缓存穿透
     * @param skuId
     * @return
     */
    public SkuDetailTo getSkuDetail1(Long skuId){
        String jsonStr = redisTemplate.opsForValue().get("sku:info:" + skuId);
        //redis中没有
        if (StringUtils.isEmpty(jsonStr)){
            //从数据库中查
            SkuDetailTo skuDetailRPC = getSkuDetailRPC(skuId);
            //保存到redis
            redisTemplate.opsForValue().set("sku:info:"+ skuId,Jsons.toStr(skuDetailRPC));
            return skuDetailRPC;
        }else {
            return Jsons.toObj(jsonStr,SkuDetailTo.class);
        }

    }


    public SkuDetailTo getSkuDetailRPC(Long skuId) {

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

        //json
        CompletableFuture<Void> skuValueJsonFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<String> skuValueJsonResult = skuDetailFeignClient.getSkuValueJson(skuInfo.getSpuId());
            String skuValueJson = skuValueJsonResult.getData();
            skuDetailTo.setValuesSkuJson(skuValueJson);
        }, threadPoolExecutor);

        CompletableFuture.allOf(
                skuImageListFuture,
                priceResultFuture,
                saleAttrFuture,
                categoryFuture,
                skuValueJsonFuture
        ).join();

        return skuDetailTo;
    }


}
