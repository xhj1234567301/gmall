package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 懒羊
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Mapper
* @createDate 2022-08-22 19:38:21
* @Entity com.atguigu.gmall.product.domain.SpuSaleAttr
*/
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    List<SpuSaleAttr> getSaleAttrAndValueMarkSku(@Param("spuId") Long spuId, @Param("skuId") Long skuId);
}




