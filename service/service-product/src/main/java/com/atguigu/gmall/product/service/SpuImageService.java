package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuImage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 懒羊
* @description 针对表【spu_image(商品图片表)】的数据库操作Service
* @createDate 2022-08-22 19:38:21
*/
public interface SpuImageService extends IService<SpuImage> {

    List<SpuImage> getSpuImageList(String spuId);
}
