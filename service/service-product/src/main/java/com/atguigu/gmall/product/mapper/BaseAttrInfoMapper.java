package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 懒羊
* @description 针对表【base_attr_info(属性表)】的数据库操作Mapper
* @createDate 2022-08-22 19:38:20
* @Entity com.atguigu.gmall.product.domain.BaseAttrInfo
*/
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    /**
     * 获取平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<BaseAttrInfo> getAttrInfoLis(@Param("category1Id") Long category1Id, @Param("category2Id") Long category2Id, @Param("category3Id") Long category3Id);
}




