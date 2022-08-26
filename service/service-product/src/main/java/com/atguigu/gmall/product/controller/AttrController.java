package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/08/23/2022/8/23
 */

@Api(tags = "库存管理")
@RequestMapping("/admin/product") //抽取公共路径
@RestController
public class AttrController {

    @Autowired
    BaseAttrInfoService baseAttrInfoService;

    @Autowired
    BaseAttrValueService baseAttrValueService;

    /**
     * 根据分类id获取平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @ApiOperation("根据分类id获取平台属性")
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable("category1Id")Long category1Id,
                               @PathVariable("category2Id")Long category2Id,
                               @PathVariable("category3Id")Long category3Id){
        List<BaseAttrInfo> attrInfoList= baseAttrInfoService.attrInfoList(category1Id,category2Id,category3Id);
        return Result.ok(attrInfoList);
    }

    /**
     * 根据平台属性ID获取平台属性对象数据
     * @param attrId
     * @return
     */
    @ApiOperation("根据平台属性ID获取平台属性对象数据")
    @GetMapping("/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId") Long attrId){
        List<BaseAttrValue> baseAttrValueList = baseAttrValueService.getAttrValueList(attrId);
        return Result.ok(baseAttrValueList);
    }

    /**
     * 添加平台属性 / 修改平台属性
     * @return
     */
    @ApiOperation("添加平台属性 / 修改平台属性")
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }



}
