package com.atguigu.gmall.product.service.impl;
import com.atguigu.gmall.model.list.SearchAttr;
import com.google.common.collect.Lists;
import java.util.Date;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Autowired
    BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    SearchFeignClient searchFeignClient;

    @Autowired
    BaseTrademarkService baseTrademarkService;


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
        skuSaleAttrValueService.saveBatch(skuSaleAttrValueList);
        //把这个SkuId放到布隆过滤器中
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        bloomFilter.add(skuId);
    }

    @Override
    public void setOnSale(Long skuId) {
        skuInfoMapper.updateIsSale(skuId,1);
        //TODO 2、给es中保存这个商品，商品就能被检索到了
        Goods goods = getGoodsBySkuId(skuId);
        searchFeignClient.saveGood(goods);
    }

    @Override
    public void cancelOnSale(Long skuId) {
        skuInfoMapper.updateIsSale(skuId,0);
        //TODO 2、从es中删除这个商品
        searchFeignClient.delete(skuId);
    }

    /**
     * 查询商品详细信息
     * @param skuId
     * @return
     */
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        SkuDetailTo skuDetailTo = new SkuDetailTo();

        //sku基本信息
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        skuDetailTo.setSkuInfo(skuInfo);

        //skuImageList
        List<SkuImage> imageList = skuImageService.getSkuImageList(skuId);
        skuInfo.setSkuImageList(imageList);

        //sku所属的分类信息
        CategoryViewTo categoryViewTo = baseCategory3Mapper.getCategoryViewTo(skuInfo.getCategory3Id());
        skuDetailTo.setCategoryView(categoryViewTo);

        //实时价格
        BigDecimal price = get1010Price(skuId);
        skuDetailTo.setPrice(price);

        //(√)4、商品（sku）所属的SPU当时定义的所有销售属性名值组合（固定好顺序）。
        //          spu_sale_attr、spu_sale_attr_value
        //          并标识出当前sku到底spu的那种组合，页面要有高亮框 sku_sale_attr_value
        //查询当前sku对应的spu定义的所有销售属性名和值（固定好顺序）并且标记好当前sku属于哪一种组合
        List<SpuSaleAttr> saleAttrList = spuSaleAttrService.getSaleAttrAndValueMarkSku(skuInfo.getSpuId(),skuId);
        skuDetailTo.setSpuSaleAttrList(saleAttrList);

        //valueSkuJson {"119/20":50;......}

        return skuDetailTo;
    }

    @Override
    public BigDecimal get1010Price(Long skuId) {
        //性能低下
        BigDecimal price = skuInfoMapper.getRealPrice(skuId);
        return price;
    }

    @Override
    public SkuInfo getDetailSkuInfo(Long skuId) {
        //sku基本信息
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo;
    }

    @Override
    public List<SkuImage> getSkuImageList(Long skuId) {
        List<SkuImage> imageList = skuImageService.getSkuImageList(skuId);
        return imageList;
    }

    @Override
    public List<Long> getSkuIdList() {
        return skuInfoMapper.getSkuIdList();
    }

    @Override
    public Goods getGoodsBySkuId(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        Goods goods = new Goods();

        goods.setId(skuId);
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setCreateTime(new Date());
        //品牌信息
        goods.setTmId(skuInfo.getTmId());
        BaseTrademark baseTrademark = baseTrademarkService.getById(skuInfo.getTmId());
        goods.setTmName(baseTrademark.getTmName());
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        //三级列表
        CategoryViewTo categoryViewTo = baseCategory3Mapper.getCategoryViewTo(skuInfo.getCategory3Id());

        goods.setCategory1Id(categoryViewTo.getCategory1Id());
        goods.setCategory1Name(categoryViewTo.getCategory1Name());
        goods.setCategory2Id(categoryViewTo.getCategory2Id());
        goods.setCategory2Name(categoryViewTo.getCategory2Name());
        goods.setCategory3Id(categoryViewTo.getCategory2Id());
        goods.setCategory3Name(categoryViewTo.getCategory3Name());
        goods.setHotScore(0L);//TODO 热度分更新

        //查当前sku所有平台属性名和值
        List<SearchAttr> attrs = skuAttrValueService.getSkuAttrNameAndValue(skuId);
        goods.setAttrs(attrs);


        return goods;
    }
}




