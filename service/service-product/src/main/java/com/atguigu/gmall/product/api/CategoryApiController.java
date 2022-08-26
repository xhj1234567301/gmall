package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/08/26/2022/8/26
 */
@Api(tags = "三级分类查询RPC接口")
@RequestMapping("/api/inner/rpc/product")
@RestController
public class CategoryApiController {

    @Autowired
    BaseCategory3Service baseCategory3Service;

    @ApiOperation("三级分类树形结构查询")
    @GetMapping("/category/tree")
    public Result getCategoryTree(){
        List<CategoryTreeTo> categoryTreeTos = baseCategory3Service.getCategoryTree();
        return Result.ok(categoryTreeTos);
    }
}
