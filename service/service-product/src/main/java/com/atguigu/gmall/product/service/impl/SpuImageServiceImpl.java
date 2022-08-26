package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuImage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.mapper.SpuImageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 懒羊
* @description 针对表【spu_image(商品图片表)】的数据库操作Service实现
* @createDate 2022-08-22 19:38:21
*/
@Service
public class SpuImageServiceImpl extends ServiceImpl<SpuImageMapper, SpuImage>
    implements SpuImageService{

    @Autowired
    SpuImageMapper spuImageMapper;

    @Override
    public List<SpuImage> getSpuImageList(String spuId) {
        return spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id",spuId));
    }
}




