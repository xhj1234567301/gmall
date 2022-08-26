package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
@Api(tags = "SPU管理")
@RestController
@RequestMapping("/admin/product")
public class SpuController {

    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    BaseSaleAttrService baseSaleAttrService;

    @ApiOperation("获取spu分页列表")
    @GetMapping("{pageNum}/{pageSize}")
    public Result getSpuList(@PathVariable("pageNum")Long pageNum,
                             @PathVariable("pageSize")Long pageSize,
                             @RequestParam("category3Id") Long category3Id){
        Page<SpuInfo> spuInfoPage = new Page<>(pageNum, pageSize);
        Page<SpuInfo> infoPage = spuInfoService.page(spuInfoPage,new QueryWrapper<SpuInfo>().eq("category3_id",category3Id));
        return Result.ok(infoPage);
    }

    @ApiOperation("获取销售属性")
    @GetMapping("baseSaleAttrList")
    public Result getBaseSaleAttrList(){
        List<BaseSaleAttr> list = baseSaleAttrService.list();
        return Result.ok(list);
    }

    @ApiOperation("添加spu")
    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        spuInfoService.saveSpuInfo(spuInfo);
        return Result.ok();
    }


}
