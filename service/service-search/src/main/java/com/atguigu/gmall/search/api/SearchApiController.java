package com.atguigu.gmall.search.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.SearchResponseVo;
import com.atguigu.gmall.model.vo.SearchParamVo;
import com.atguigu.gmall.search.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: LAZY
 * @Date: 2022/09/05/2022/9/5
 */
@RequestMapping("/api/inner/rpc/search")
@RestController
public class SearchApiController {

    @Autowired
    GoodsService goodsService;


    /**
     * es添加上架商品
     * @param goods
     * @return
     */
    @PostMapping("goods")
    public Result saveGood(@RequestBody Goods goods){
        goodsService.saveGood(goods);
        return Result.ok();
    }

    /**
     * es删除下架商品
     * @param id
     * @return
     */
    @DeleteMapping("goods/{id}")
    public Result deleteGood(@PathVariable("id") Long id){
        goodsService.deleteGood(id);
        return Result.ok();

    }

    /**
     * es检索
     * @param paramVo
     * @return
     */
    @PostMapping("/goods/search")
    public Result<SearchResponseVo> search(@RequestBody SearchParamVo paramVo){
        SearchResponseVo searchResponseVo = goodsService.search(paramVo);
        return Result.ok(searchResponseVo);
    }

    /**
     * 更新热度分
     * @param skuId
     * @param score
     * @return
     */
    @GetMapping("/goods/hotscore/{skuId}")
    public Result updateHotScore(@PathVariable("skuId") Long skuId,
                          @RequestParam("score") Long score){
        goodsService.updateHotScore(skuId,score);
        return Result.ok();

    }



}
