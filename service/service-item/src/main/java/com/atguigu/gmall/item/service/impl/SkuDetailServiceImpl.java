package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.feign.product.SkuProductFeignClient;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.starter.cache.annotation.GmallCaches;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    SkuProductFeignClient skuDetailFeignClient;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SearchFeignClient searchFeignClient;



    @GmallCaches(
            cacheKey = SysRedisConst.SKU_INFO_PREFIX+"#{#params[0]}",
            bloomName = SysRedisConst.BLOOM_SKUID,
            bloomValue = "#{#params[0]}",
            lockName = SysRedisConst.LOCK_SKU_DETAIL+"#{#params[0]}",
            ttl = 60*60*24*7L
    )
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        SkuDetailTo skuDetailRPC = getSkuDetailRPC(skuId);
        return skuDetailRPC;
    }

    /**
     * 更新热度分
     * @param skuId
     */
    @Override
    public void updateHotScore(Long skuId) {
        Long increment = redisTemplate.opsForValue().increment(SysRedisConst.SKU_HOTSCORE_PREFIX + skuId);
        if (increment%100 == 0){
            searchFeignClient.updateHotScore(skuId,increment);
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
            if(skuInfo != null) {
                Result<CategoryViewTo> categoryViewToResult = skuDetailFeignClient.getCategoryViewTo(skuInfo.getCategory3Id());
                CategoryViewTo categoryViewTo = categoryViewToResult.getData();
                skuDetailTo.setCategoryView(categoryViewTo);
            }
        }, threadPoolExecutor);


        //4.图片信息
        CompletableFuture<Void> skuImageListFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if(skuInfo != null){
                Result<List<SkuImage>> skuImageListResult = skuDetailFeignClient.getSkuImageList(skuId);
                List<SkuImage> skuImageList = skuImageListResult.getData();
                skuInfo.setSkuImageList(skuImageList);
            }
        }, threadPoolExecutor);


        // 商品（sku）所属的SPU当时定义的所有销售属性名值组合
        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if(skuInfo != null) {
                Result<List<SpuSaleAttr>> saleAttrAndValueMarkSkuResult = skuDetailFeignClient.getSaleAttrAndValueMarkSku(skuInfo.getSpuId(), skuId);
                List<SpuSaleAttr> saleAttrAndValueMarkSku = saleAttrAndValueMarkSkuResult.getData();
                skuDetailTo.setSpuSaleAttrList(saleAttrAndValueMarkSku);
            }
        }, threadPoolExecutor);

        //json
        CompletableFuture<Void> skuValueJsonFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if(skuInfo != null) {
                Result<String> skuValueJsonResult = skuDetailFeignClient.getSkuValueJson(skuInfo.getSpuId());
                String skuValueJson = skuValueJsonResult.getData();
                skuDetailTo.setValuesSkuJson(skuValueJson);
            }
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



    /**
     * 查询 不使用Aop
     * @param skuId
     * @return
     */
//    public SkuDetailTo getSkuDetailNoAOP(Long skuId) {
//        String cacheKey = SysRedisConst.SKU_INFO_PREFIX +skuId;
//        //1.查缓存
//        SkuDetailTo cacheData = cacheOpsService.getCacheData(cacheKey,SkuDetailTo.class);
//        //2.缓存没有
//        if (cacheData == null){
//            //3.查看布隆过滤器
//            Boolean isContain = cacheOpsService.bloomContains(skuId);
//            if (! isContain){
//                //布隆中没有 肯定没有
//                log.info("[{}]商品 - 布隆判定没有，检测到隐藏的攻击风险....",skuId);
//                return null;
//            }
//            //4.有 回源
//            //防止缓存击穿 加锁
//            boolean lock = cacheOpsService.tryLock(skuId);
//            if (lock){
//                //回源
//                //查询
//                log.info("[{}]商品 缓存未命中，布隆说有，准备回源.....",skuId);
//                SkuDetailTo skuDetailRPC = getSkuDetailRPC(skuId);
//                //放入缓存 空值也缓存
//                cacheOpsService.saveData(cacheKey,skuDetailRPC);
//                //解锁
//                cacheOpsService.unLock(skuId);
//                return skuDetailRPC;
//            }
//            //没获取到锁 从缓存中查
//            try {
//                Thread.sleep(1000);
//                return cacheOpsService.getCacheData(cacheKey,SkuDetailTo.class);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        //5.缓存中有
//        return cacheData;
//    }

    /**
     * 添加redis缓存 null（x）值缓存  未添加redisson分布式锁 以及布隆过滤器
     * @param skuId
     * @return
     */
    public SkuDetailTo getSkuDetailNoLockNoRedission(Long skuId){
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




}
