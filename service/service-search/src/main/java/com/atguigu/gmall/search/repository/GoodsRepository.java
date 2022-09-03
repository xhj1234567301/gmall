package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @Author: LAZY
 * @Date: 2022/09/03/2022/9/3
 */
public interface GoodsRepository extends PagingAndSortingRepository<Goods,Long> {
}
