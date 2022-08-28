package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 懒羊
* @description 针对表【base_category3(三级分类表)】的数据库操作Service
* @createDate 2022-08-22 19:38:20
*/
public interface BaseCategory3Service extends IService<BaseCategory3> {

    List<BaseCategory3> getCategory3(String category2Id);

    List<CategoryTreeTo> getCategoryTree();

    CategoryViewTo getCategoryViewTo(Long c3Id);
}
