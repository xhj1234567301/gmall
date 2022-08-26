package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/08/24/2022/8/24
 */
@Api(tags = "SKU管理")
@RestController
@RequestMapping("/admin/product")
public class SkuController {

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SpuImageService spuImageService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @ApiOperation("获取sku分页列表")
    @GetMapping("list/{pageNum}/{pageSize}")
    public Result getSkuList(@PathVariable("pageNum")Long pageNum,
                             @PathVariable("pageSize")Long pageSize){
        Page<SkuInfo> skuInfoPage = new Page<>(pageNum, pageSize);
        Page<SkuInfo> page = skuInfoService.page(skuInfoPage);
        return Result.ok(page);
    }

    @ApiOperation("根据spuId获取图片列表")
    @GetMapping("spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable("spuId")String spuId){
        List<SpuImage> list = spuImageService.getSpuImageList(spuId);
        return Result.ok(list);
    }

    @ApiOperation("根据spuId获取销售属性")
    @GetMapping("spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable("spuId")String spuId){
        List<SpuSaleAttr> list = spuSaleAttrService.getSpuSaleAttrList(spuId);
        return Result.ok(list);
    }

    @ApiOperation("添加Sku")
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        skuInfoService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    @ApiOperation("上架")
    @GetMapping("/onSale/{skuId}")
    public Result setOnSale(@PathVariable("skuId")Long skuId){
        skuInfoService.setOnSale(skuId);
        return Result.ok();
    }

    @ApiOperation("下架")
    @GetMapping("/cancelSale/{skuId}")
    public Result canselOnSale(@PathVariable("skuId")Long skuId){
        skuInfoService.cancelOnSale(skuId);
        return Result.ok();
    }

}
