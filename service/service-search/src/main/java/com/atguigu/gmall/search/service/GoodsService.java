package com.atguigu.gmall.search.service;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.SearchResponseVo;
import com.atguigu.gmall.model.vo.SearchParamVo;

/**
 * @Author: LAZY
 * @Date: 2022/09/05/2022/9/5
 */
public interface GoodsService {
    void saveGood(Goods goods);

    void deleteGood(Long id);

    SearchResponseVo search(SearchParamVo paramVo);

    void updateHotScore(Long skuId, Long score);
}
