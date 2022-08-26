package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author 懒羊
* @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
* @createDate 2022-08-22 19:38:21
*/
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
    implements SkuInfoService{

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuImageService skuImageService;

    @Autowired
    SkuAttrValueService skuAttrValueService;

    /**
     * 添加sku
     * @param skuInfo
     */
    @Override
    @Transactional
    public void saveSkuInfo(SkuInfo skuInfo) {
        //保存sku
        skuInfoMapper.insert(skuInfo);
        //skuId
        Long skuId = skuInfo.getId();
        //保存skuImage
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
        }
        skuImageService.saveBatch(skuImageList);
        //保存skuSaleAttrValue
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
        }
        skuAttrValueService.saveBatch(skuAttrValueList);
        //保存skuSaleAttrValueList
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
        }

    }

    @Override
    public void setOnSale(Long skuId) {
        skuInfoMapper.updateIsSale(skuId,1);
        //TODO 2、给es中保存这个商品，商品就能被检索到了
    }

    @Override
    public void cancelOnSale(Long skuId) {
        skuInfoMapper.updateIsSale(skuId,0);
        //TODO 2、从es中删除这个商品
    }
}




