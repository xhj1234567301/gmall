package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @Author: LAZY
 * @Date: 2022/08/23/2022/8/23
 */
@Api(tags = "品牌管理")
@RestController
@RequestMapping("/admin/product")
public class BaseTradeMarkController {

    @Autowired
    BaseTrademarkService baseTrademarkService;

    /**
     * 获取品牌分页列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation("获取品牌分页列表")
    @GetMapping("/baseTrademark/{pageNum}/{pageSize}")
    public Result getBaseTrademark(@PathVariable("pageNum")Long pageNum,
                                   @PathVariable("pageSize") Long pageSize){
        Page<BaseTrademark> page = new Page<>(pageNum, pageSize);
        //分页查询
        Page<BaseTrademark> pageResult = baseTrademarkService.page(page);
        return Result.ok(pageResult);
    }

    @ApiOperation("根据品牌id查询品牌信息")
    @GetMapping("/baseTrademark/get/{id}")
    public Result getById(@PathVariable("id") Long id){
        BaseTrademark baseTrademark = baseTrademarkService.getById(id);
        return Result.ok(baseTrademark);
    }

    @ApiOperation("获取品牌属性")
    @GetMapping("baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        List<BaseTrademark> list = baseTrademarkService.list();
        return Result.ok(list);
    }

    /**
     * 添加品牌
     * @param baseTrademark
     * @return
     */
    @ApiOperation("添加品牌")
    @PostMapping("/baseTrademark/save")
    public Result addBaseTrademark(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    /**
     * 修改品牌
     * @param baseTrademark
     * @return
     */
    @ApiOperation("修改品牌")
    @PutMapping("/baseTrademark/update")
    public Result updateBaseTrademark(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    /**
     * 删除品牌
     * @param id
     * @return
     */
    @ApiOperation("删除品牌")
    @DeleteMapping("baseTrademark/remove/{id}")
    public Result deleteBaseTradeMark(@PathVariable("id") Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

}
