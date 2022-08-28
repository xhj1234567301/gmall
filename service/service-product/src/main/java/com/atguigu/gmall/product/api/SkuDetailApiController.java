package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/08/26/2022/8/26
 */
@Api(tags = "sku查询RPC接口")
@RestController
@RequestMapping("/api/inner/rpc/product")
public class SkuDetailApiController {

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    BaseCategory3Service baseCategory3Service;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    /**
     * 总的请求
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/{skuId}")
    public Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId") Long skuId){
        SkuDetailTo skuDetailTo = skuInfoService.getSkuDetail(skuId);
        return  Result.ok(skuDetailTo);
    }

    //getSkuDetail拆分为小的请求

    /**
     * skuIndo
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/info/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable("skuId")Long skuId){
        SkuInfo skuInfo = skuInfoService.getDetailSkuInfo(skuId);
        return Result.ok(skuInfo);
    }

    /**
     * skuImageList
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/imageList/{skuId}")
    public Result<List<SkuImage>> getSkuImageList(@PathVariable("skuId")Long skuId){
        List<SkuImage> imageList = skuInfoService.getSkuImageList(skuId);
        return Result.ok(imageList);
    }

    /**
     * 分类信息
     * @param c3Id
     * @return
     */
    @GetMapping("/skudetail/category/{c3Id}")
    public Result<CategoryViewTo> getCategoryViewTo(@PathVariable("c3Id")Long c3Id){
        CategoryViewTo categoryViewTo = baseCategory3Service.getCategoryViewTo(c3Id);
        return Result.ok(categoryViewTo);
    }

    /**
     * 查询实时价格
     * @param skuId
     * @return
     */
    @GetMapping("skudetail/price/{skuId}")
    public Result<BigDecimal> getPrice(@PathVariable("skuId")Long skuId){
        BigDecimal price = skuInfoService.get1010Price(skuId);
        return Result.ok(price);
    }

    /**
     * 商品（sku）所属的SPU当时定义的所有销售属性名值组合
     * @param spuId
     * @param skuId
     * @return
     */
    @GetMapping("skudetail/getSaleAttrAndValueMarkSku/{spuId}/{skuId}")
    public Result<List<SpuSaleAttr>> getSaleAttrAndValueMarkSku(@PathVariable("spuId") Long spuId,
                                                                @PathVariable("skuId") Long skuId){
        List<SpuSaleAttr> saleAttrList = spuSaleAttrService.getSaleAttrAndValueMarkSku(spuId, skuId);
        return Result.ok(saleAttrList);
    }

    /**
     *查sku组合 valueJson
     * @param spuId
     * @return
     */
    @GetMapping("skudetail/valueJson/{spuId}")
    public Result<String> getSkuValueJson(@PathVariable("spuId")Long spuId){
        String valueJson = spuSaleAttrService.getAllSkuSaleAttrValueJson(spuId);
        return Result.ok(valueJson);
    }

}
