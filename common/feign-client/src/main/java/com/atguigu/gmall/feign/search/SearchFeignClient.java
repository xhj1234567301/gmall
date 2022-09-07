package com.atguigu.gmall.feign.search;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.SearchParamVo;
import com.atguigu.gmall.model.vo.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: LAZY
 * @Date: 2022/09/05/2022/9/5
 */

@RequestMapping("/api/inner/rpc/search")
@FeignClient("service-search")
public interface SearchFeignClient {

    @PostMapping("goods")
    Result saveGood(@RequestBody Goods goods);

    @DeleteMapping("goods/{id}")
    Result delete(@PathVariable("id") Long id);

    @PostMapping("/goods/search")
    Result<SearchResponseVo> search(@RequestBody SearchParamVo paramVo);

    /**
     * 更新热度分
     * @param skuId
     * @param increment
     */
    @GetMapping("/goods/hotscore/{skuId}")
    Result updateHotScore(@PathVariable("skuId") Long skuId,
                          @RequestParam("score") Long score);
}
