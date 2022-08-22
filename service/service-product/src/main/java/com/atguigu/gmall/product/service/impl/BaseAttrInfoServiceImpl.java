package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 懒羊
* @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
* @createDate 2022-08-22 19:38:20
*/
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
    implements BaseAttrInfoService{

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Override
    public List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id) {

        QueryWrapper<BaseAttrInfo> queryWrapper = new QueryWrapper<>();

        if (category1Id != 0){
            queryWrapper.eq("category_id",category1Id).eq("category_level",1);
        }
        if (category2Id != 0){
            queryWrapper.eq("category_id",category2Id).eq("category_level",2);
        }
        if (category3Id != 0){
            queryWrapper.eq("category_id",category3Id).eq("category_level",3);
        }

        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.selectList(queryWrapper);
        return baseAttrInfos;
    }

    /**
     * 添加平台属性
     * @param baseAttrInfo
     */
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        //修改
        if (baseAttrInfo.getId() != null){

        }
        else {
            //新增

        }

    }
}




