package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SpuSaleAttrValueMapper;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 懒羊
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service实现
* @createDate 2022-08-22 19:38:21
*/
@Service
public class SpuSaleAttrServiceImpl extends ServiceImpl<SpuSaleAttrMapper, SpuSaleAttr>
    implements SpuSaleAttrService{

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {
        List<SpuSaleAttr> list = spuSaleAttrMapper.selectList(new QueryWrapper<SpuSaleAttr>().eq("spu_id", spuId));
        for (SpuSaleAttr spuSaleAttr : list) {
            QueryWrapper<SpuSaleAttrValue> queryWrapper = new QueryWrapper<SpuSaleAttrValue>().eq("spu_id", spuId).eq("base_sale_attr_id", spuSaleAttr.getBaseSaleAttrId());
            List<SpuSaleAttrValue> spuSaleAttrValues = spuSaleAttrValueMapper.selectList(queryWrapper);
            spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValues);
        }
        return list;
    }
}




