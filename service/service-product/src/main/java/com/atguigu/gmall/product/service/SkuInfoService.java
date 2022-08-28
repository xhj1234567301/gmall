package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
* @author 懒羊
* @description 针对表【sku_info(库存单元表)】的数据库操作Service
* @createDate 2022-08-22 19:38:21
*/
public interface SkuInfoService extends IService<SkuInfo> {

    void saveSkuInfo(SkuInfo skuInfo);

    void setOnSale(Long skuId);

    void cancelOnSale(Long skuId);

    SkuDetailTo getSkuDetail(Long skuId);

    public BigDecimal get1010Price(Long skuId);

    SkuInfo getDetailSkuInfo(Long skuId);

    List<SkuImage> getSkuImageList(Long skuId);
}
