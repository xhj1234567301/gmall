package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id) {

        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.getAttrInfoLis(category1Id,category2Id,category3Id);
        return baseAttrInfos;
    }

    /**
     * 添加/修改 平台属性
     * @param baseAttrInfo
     */
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        //修改
        if (baseAttrInfo.getId() != null){
            //修改info信息
            baseAttrInfoMapper.updateById(baseAttrInfo);
            List<BaseAttrValue> valueList = baseAttrInfo.getAttrValueList();
            //存在的属性id
            List<Long> vids = new ArrayList<>();
            for (BaseAttrValue baseAttrValue : valueList) {
                Long id = baseAttrValue.getAttrId();
                if (id != null){
                    vids.add(id);
                }
            }
            if(vids.size()>0){
                //删除不存在的属性
                QueryWrapper<BaseAttrValue> deleteWrapper = new QueryWrapper<>();
                deleteWrapper.eq("attr_id", baseAttrInfo.getId());
                deleteWrapper.notIn("id", vids);
                baseAttrValueMapper.delete(deleteWrapper);
            }else {
                //全部删除
                QueryWrapper<BaseAttrValue> deleteWrapper = new QueryWrapper<>();
                deleteWrapper.eq("attr_id", baseAttrInfo.getId());
                baseAttrValueMapper.delete(deleteWrapper);
            }

            //修改value信息
            for (BaseAttrValue baseAttrValue : valueList) {
                //修改
                if (baseAttrValue.getId() != null){
                    baseAttrValueMapper.updateById(baseAttrValue);
                }
                //新增
                else {
                    baseAttrValue.setAttrId(baseAttrInfo.getId());
                    baseAttrValueMapper.insert(baseAttrValue);
                }
            }

        }
        else {
            insertAttrInfo(baseAttrInfo);

        }

    }

    private void insertAttrInfo(BaseAttrInfo baseAttrInfo) {
        //新增
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        //添加到info
        baseAttrInfoMapper.insert(baseAttrInfo);
        Long id = baseAttrInfo.getId();
        //添加attr
        for (BaseAttrValue attrValue : attrValueList) {
            attrValue.setAttrId(id);
            baseAttrValueMapper.insert(attrValue);
        }
    }
}




