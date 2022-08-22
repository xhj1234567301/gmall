package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/08/22/2022/8/22
 */
@RequestMapping("/admin/product") //抽取公共路径
@RestController
public class CategoryController {

    @Autowired
    BaseCategory1Service baseCategory1Service;

    @Autowired
    BaseCategory2Service baseCategory2Service;

    @Autowired
    BaseCategory3Service baseCategory3Service;

    @Autowired
    BaseAttrInfoService baseAttrInfoService;

    @Autowired
    BaseAttrValueService baseAttrValueService;


    /**
     * 获取所有的一级分类
     * @GetMapping：GET请求
     * @PostMapping：POST请求
     */
    @GetMapping("/getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> category1List = baseCategory1Service.list();
        return Result.ok(category1List);
    }

    /**
     * 根据一级分类获取所有二级分类
     * @param category1Id
     * @return
     */
    @GetMapping("/getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable("category1Id") String category1Id){
        List<BaseCategory2> category2List  = baseCategory2Service.getCategory2(category1Id);
        return Result.ok(category2List);
    }

    /**
     * 根据二级分类获取所有三级分类
     * @param category2Id
     * @return
     */
    @GetMapping("/getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable("category2Id") String category2Id){
        List<BaseCategory3> category3List = baseCategory3Service.getCategory3(category2Id);
        return Result.ok(category3List);
    }

    /**
     * 根据分类id获取平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable("category1Id")Long category1Id,
                               @PathVariable("category2Id")Long category2Id,
                               @PathVariable("category3Id")Long category3Id){
        List<BaseAttrInfo> attrInfoList= baseAttrInfoService.attrInfoList(category1Id,category2Id,category3Id);
        return Result.ok(attrInfoList);
    }

    /**
     * 添加平台属性 / 修改平台属性
     * @return
     */
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 根据平台属性ID获取平台属性对象数据
     * @param attrId
     * @return
     */
    @GetMapping("/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId") Long attrId){
        List<BaseAttrValue> baseAttrValueList = baseAttrValueService.getAttrValueList(attrId);
        return Result.ok(baseAttrValueList);
    }


}
