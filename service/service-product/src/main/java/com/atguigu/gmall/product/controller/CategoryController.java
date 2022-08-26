package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.*;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/08/22/2022/8/22
 */
@Api(tags = "三级分类")
@RequestMapping("/admin/product") //抽取公共路径
@RestController
public class CategoryController {

    @Autowired
    BaseCategory1Service baseCategory1Service;

    @Autowired
    BaseCategory2Service baseCategory2Service;

    @Autowired
    BaseCategory3Service baseCategory3Service;



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





}
