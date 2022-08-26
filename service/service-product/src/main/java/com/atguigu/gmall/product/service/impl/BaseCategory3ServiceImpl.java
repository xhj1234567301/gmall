package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 懒羊
* @description 针对表【base_category3(三级分类表)】的数据库操作Service实现
* @createDate 2022-08-22 19:38:20
*/
@Service
public class BaseCategory3ServiceImpl extends ServiceImpl<BaseCategory3Mapper, BaseCategory3>
    implements BaseCategory3Service{

    @Autowired
    BaseCategory3Mapper baseCategory3Mapper;

    @Override
    public List<BaseCategory3> getCategory3(String category2Id) {
        return baseCategory3Mapper.selectList(new QueryWrapper<BaseCategory3>().eq("category2_id",category2Id));
    }

    @Override
    public List<CategoryTreeTo> getCategoryTree() {

        List<CategoryTreeTo> list = baseCategory3Mapper.getCategoryTree();
        return list;
    }
}




