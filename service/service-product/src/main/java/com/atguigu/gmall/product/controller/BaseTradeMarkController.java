package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/08/23/2022/8/23
 */
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
    @GetMapping("/baseTrademark/{pageNum}/{pageSize}")
    public Result getBaseTrademark(@PathVariable("pageNum")Long pageNum,
                                   @PathVariable("pageSize") Long pageSize){
        Page<BaseTrademark> page = new Page<>(pageNum, pageSize);
        //分页查询
        Page<BaseTrademark> pageResult = baseTrademarkService.page(page);
        return Result.ok(pageResult);
    }

    @GetMapping("/baseTrademark/get/{id}")
    public Result getById(@PathVariable("id") Long id){
        BaseTrademark baseTrademark = baseTrademarkService.getById(id);
        return Result.ok(baseTrademark);
    }

    /**
     * 添加品牌
     * @param baseTrademark
     * @return
     */
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
    @DeleteMapping("baseTrademark/remove/{id}")
    public Result deleteBaseTradeMark(@PathVariable("id") Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

}
