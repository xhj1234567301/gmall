package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.model.to.ValueSkuJsonTo;
import com.atguigu.gmall.product.mapper.SpuSaleAttrValueMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author 懒羊
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service实现
* @createDate 2022-08-22 19:38:21
*/
@Service
public class SpuSaleAttrServiceImpl extends ServiceImpl<SpuSaleAttrMapper, SpuSaleAttr>
    implements SpuSaleAttrService{

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {
        List<SpuSaleAttr> list = spuSaleAttrMapper.getSaleAttrAndValueBySpuId(spuId);
        return list;
    }

    @Override
    public List<SpuSaleAttr> getSaleAttrAndValueMarkSku(Long spuId, Long skuId) {
        List<SpuSaleAttr> list = spuSaleAttrMapper.getSaleAttrAndValueMarkSku(spuId,skuId);
        return list;
    }

    /**
     * sku组合
     * {"118|120":49,"119|121":50}
     * @param spuId
     * @return
     */
    @Override
    public String getAllSkuSaleAttrValueJson(Long spuId) {
        List<ValueSkuJsonTo> valueSkuJsonTos = spuSaleAttrMapper.getAllSkuValueJson(spuId);
        // {"118|120":49,"119|121":50}
        // StreamAPI  lambda；
        Map<String,Long> map = new HashMap<>();
        for (ValueSkuJsonTo valueSkuJsonTo :valueSkuJsonTos){
            String valueJson = valueSkuJsonTo.getValueJson(); // 118|120
            Long skuId = valueSkuJsonTo.getSkuId(); // 49
            map.put(valueJson,skuId);
        }
        //fastjson  springboot: jackson
        String json = Jsons.toStr(map);
        System.out.println("json = " + json);
        return json;
    }
}




