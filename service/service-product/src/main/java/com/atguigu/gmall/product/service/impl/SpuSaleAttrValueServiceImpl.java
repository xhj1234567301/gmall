package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.atguigu.gmall.product.mapper.SpuSaleAttrValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 懒羊
* @description 针对表【spu_sale_attr_value(spu销售属性值)】的数据库操作Service实现
* @createDate 2022-08-22 19:38:21
*/
@Service
public class SpuSaleAttrValueServiceImpl extends ServiceImpl<SpuSaleAttrValueMapper, SpuSaleAttrValue>
    implements SpuSaleAttrValueService{

    @Autowired
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;


}




