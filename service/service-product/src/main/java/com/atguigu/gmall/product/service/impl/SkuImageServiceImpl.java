package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SkuImage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.mapper.SkuImageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 懒羊
* @description 针对表【sku_image(库存单元图片表)】的数据库操作Service实现
* @createDate 2022-08-22 19:38:21
*/
@Service
public class SkuImageServiceImpl extends ServiceImpl<SkuImageMapper, SkuImage>
    implements SkuImageService{

    @Autowired
    SkuImageMapper skuImageMapper;

    @Override
    public List<SkuImage> getSkuImageList(Long skuId) {
        List<SkuImage> list = skuImageMapper.selectList(new QueryWrapper<SkuImage>().eq("sku_id",skuId));
        return list;
    }
}




