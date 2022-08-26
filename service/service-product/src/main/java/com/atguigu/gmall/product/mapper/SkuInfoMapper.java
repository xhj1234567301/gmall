package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
* @author 懒羊
* @description 针对表【sku_info(库存单元表)】的数据库操作Mapper
* @createDate 2022-08-22 19:38:21
* @Entity com.atguigu.gmall.product.domain.SkuInfo
*/
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    void updateIsSale(Long skuId,  int status);

    BigDecimal getRealPrice(@Param("skuId") Long skuId);
}




