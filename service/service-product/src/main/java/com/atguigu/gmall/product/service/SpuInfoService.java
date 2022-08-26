package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 懒羊
* @description 针对表【spu_info(商品表)】的数据库操作Service
* @createDate 2022-08-22 19:38:21
*/
public interface SpuInfoService extends IService<SpuInfo> {

    void saveSpuInfo(SpuInfo spuInfo);
}
