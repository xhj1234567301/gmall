package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.vo.*;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.service.GoodsService;
import com.google.common.collect.Lists;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/09/05/2022/9/5
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    ElasticsearchRestTemplate esRestTemplate;

    @Override
    public void saveGood(Goods goods) {
        goodsRepository.save(goods);
    }

    @Override
    public void deleteGood(Long id) {
        goodsRepository.deleteById(id);
    }

    /**
     * es检索返回需要显示的数据
     * @param paramVo
     * @return
     */
    @Override
    public SearchResponseVo search(SearchParamVo paramVo) {

        //1、动态构建出搜索条件
        Query query = buildQueryDsl(paramVo);


        //2、搜索
        SearchHits<Goods> search = esRestTemplate.search(query,Goods.class, IndexCoordinates.of("goods"));

        //3、将搜索结果进行转换
        SearchResponseVo responseVo = buildSearchResponseResult(search,paramVo);

        return responseVo;
    }

    /**
     * 更新热度分
     * @param skuId
     * @param score
     */
    @Override
    public void updateHotScore(Long skuId, Long score) {

        //1.找到商品
        Goods goods = goodsRepository.findById(skuId).get();
        //2.更新热度分
        goods.setHotScore(score);
        //3.同步到es
        goodsRepository.save(goods);

    }

    /**
     * 根据检索到的记录，构建响应结果
     * @param goods
     * @param paramVo
     * @return
     */
    private SearchResponseVo buildSearchResponseResult(SearchHits<Goods> goods, SearchParamVo paramVo) {
        SearchResponseVo vo = new SearchResponseVo();

        //1、当时检索前端传来的所有参数
        vo.setSearchParam(paramVo);
        //2. 构建品牌面包屑 trademark=1:小米
        if (!StringUtils.isEmpty(paramVo.getTrademark())){
            vo.setTrademarkParam("品牌：" + paramVo.getTrademark().split(":")[1]);
        }
        //3、平台属性面包屑
        if(paramVo.getProps()!= null && paramVo.getProps().length>0){
            String[] props = paramVo.getProps();
            List<SearchAttr> propsParamList = new ArrayList<>();
            for (String prop : props) {
                String[] split = prop.split(":");
                //一个SearchAttr 代表一个属性面包屑
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(Long.parseLong(split[0]));
                searchAttr.setAttrName(split[1]);
                searchAttr.setAttrValue(split[2]);
                propsParamList.add(searchAttr);
            }
        }

        //TODO 4、所有品牌列表 。需要ES聚合分析
        List<TrademarkVo> trademarkVoList = buildTrademarkList(goods);
        vo.setTrademarkList(trademarkVoList);
        //TODO 5、所有属性列表 。需要ES聚合分析
        List<AttrVo> attrVoList = buildAttrList(goods);
        vo.setAttrsList(attrVoList);

        //为了回显
        //6、返回排序信息  order=1:desc
        if (!StringUtils.isEmpty(paramVo.getOrder())){
            String order = paramVo.getOrder();
            String[] split = order.split(":");
            OrderMapVo orderMapVo = new OrderMapVo();
            orderMapVo.setSort(split[0]);
            orderMapVo.setSort(split[1]);
            vo.setOrderMap(orderMapVo);
        }

        //7、所有搜索到的商品列表
        List<Goods> goodsList = new ArrayList<>();
        List<SearchHit<Goods>> searchHits = goods.getSearchHits();
        for (SearchHit<Goods> searchHit : searchHits) {
            Goods content = searchHit.getContent();
            //如果模糊检索了，会有高亮标题
            if (!StringUtils.isEmpty(paramVo.getKeyword())){
                String title = searchHit.getHighlightField("title").get(0);
                content.setTitle(title);
            }
            goodsList.add(content);
        }
        vo.setGoodsList(goodsList);

        //8、页码
        vo.setPageNo(paramVo.getPageNo());

        //9、总页码？
        long totalHits = goods.getTotalHits();//总记录条数
        Long totalPage = totalHits%SysRedisConst.SEARCH_PAGE_SIZE == 0 ?
                totalHits/SysRedisConst.SEARCH_PAGE_SIZE :
                totalHits/SysRedisConst.SEARCH_PAGE_SIZE +1;
        vo.setTotalPages(new Integer(totalPage+""));

        //10、老连接。。。   /list.html?category2Id=13
        String url = makeUrlParam(paramVo);
        vo.setUrlParam(url);

        return vo;
    }

    /**
     * 所有品牌列表 。需要ES聚合分析
     * @param goods
     * @return
     */
    private List<TrademarkVo> buildTrademarkList(SearchHits<Goods> goods) {

        List<TrademarkVo> trademarkVoList = new ArrayList<>();
        //拿到 tmIdAgg 聚合
        ParsedLongTerms tmIdAgg = goods.getAggregations().get("tmIdAgg");
        //拿到品牌id桶聚合中的每个数据
        for (Terms.Bucket bucket : tmIdAgg.getBuckets()) {
            TrademarkVo trademarkVo = new TrademarkVo();

            //获取品牌id
            long id = bucket.getKeyAsNumber().longValue();
            trademarkVo.setTmId(id);

            //获取品牌名称
            ParsedStringTerms tmName = bucket.getAggregations().get("tmNameAgg");
            String tmNameString = tmName.getBuckets().get(0).getKeyAsString();
            trademarkVo.setTmName(tmNameString);

            //获取品牌logo
            ParsedStringTerms tmLogoAgg = bucket.getAggregations().get("tmLogoAgg");
            String tmLogoUrl = tmLogoAgg.getBuckets().get(0).getKeyAsString();
            trademarkVo.setTmLogoUrl(tmLogoUrl);

            trademarkVoList.add(trademarkVo);
        }
        return trademarkVoList;
    }

    /**
     * 所有属性列表 。需要ES聚合分析
     * @param goods
     * @return
     */
    private List<AttrVo> buildAttrList(SearchHits<Goods> goods) {

        List<AttrVo> attrVoList = new ArrayList<>();

        //拿到 attrAgg 聚合
        ParsedNested attrAgg = goods.getAggregations().get("attrAgg");

        //属性id
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        //3、遍历所有属性id
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            AttrVo attrVo = new AttrVo();
            //3.1、属性id
            long id = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(id);

            //3.2 属性名
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);

            //3.3 属性值
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
            List<String> attrValueList = new ArrayList<>();
            for (Terms.Bucket valueAggBucket : attrValueAgg.getBuckets()) {
                String keyAsString = valueAggBucket.getKeyAsString();
                attrValueList.add(keyAsString);
            }
            attrVo.setAttrValueList(attrValueList);

            attrVoList.add(attrVo);
        }


        return attrVoList;
    }


    /**
     * 拼接老链接
     * @param paramVo
     * @return
     */
    private String makeUrlParam(SearchParamVo paramVo) {
        // list.html?&k=v
        StrBuilder builder = new StrBuilder("list.html?");
        //1、拼三级分类所有参数
        if (!StringUtils.isEmpty(paramVo.getCategory1Id())){
            builder.append("&category1Id="+paramVo.getCategory1Id());
        }
        if (!StringUtils.isEmpty(paramVo.getCategory2Id())){
            builder.append("&category2Id="+paramVo.getCategory2Id());
        }
        if (!StringUtils.isEmpty(paramVo.getCategory3Id())){
            builder.append("&category3Id="+paramVo.getCategory3Id());
        }
        //2.关键字
        if (!StringUtils.isEmpty(paramVo.getKeyword())){
            builder.append("&keyword="+paramVo.getKeyword());
        }
        //3.品牌
        if (!StringUtils.isEmpty(paramVo.getTrademark())){
            builder.append("&trademark="+paramVo.getTrademark());
        }
        //4、拼属性
        String[] props = paramVo.getProps();
        if (props!=null && props.length > 0) {
            for (String prop : paramVo.getProps()) {
                builder.append("&props="+prop);
            }
        }

        return builder.toString();

    }

    /**
     * 根据paramVo 动态构建出搜索条件
     * @param paramVo
     * @return
     */
    private Query buildQueryDsl(SearchParamVo paramVo) {
        //准备BoolQueryBuilder
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //分类信息
        if (paramVo.getCategory1Id() != null){
            boolQuery.must(QueryBuilders.termQuery("category1Id",paramVo.getCategory1Id()));
        }
        if (paramVo.getCategory2Id() != null){
            boolQuery.must(QueryBuilders.termQuery("category2Id",paramVo.getCategory2Id()));
        }
        if (paramVo.getCategory3Id() != null){
            boolQuery.must(QueryBuilders.termQuery("category3Id",paramVo.getCategory3Id()));
        }
        //keyWord
        if (!StringUtils.isEmpty(paramVo.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("title",paramVo.getKeyword()));
        }
        //tradedmark
        if (!StringUtils.isEmpty(paramVo.getTrademark())){
           Long tmId = Long.parseLong(paramVo.getTrademark().split(":")[0]);
           boolQuery.must(QueryBuilders.termQuery("tmId",tmId));
        }
        //props
        String[] props = paramVo.getProps();
        if (props!=null && props.length > 0) {
            for (String prop : paramVo.getProps()) {
                String[] split = prop.split(":");
                Long attrId = Long.parseLong(split[0]);
                String attrValue = split[1];
                
                //构造boolQuery
                BoolQueryBuilder nestedQuery = QueryBuilders.boolQuery();
                nestedQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedQuery.must(QueryBuilders.matchQuery("attrs.attrValue",attrValue));

                // 嵌入式查询
                NestedQueryBuilder attrs = QueryBuilders.nestedQuery("attrs", nestedQuery, ScoreMode.None);
                boolQuery.must(attrs);

            }
        }

        //准备一个原生检索条件【原生的dsl】
        NativeSearchQuery query = new NativeSearchQuery(boolQuery);

        //sort
        if (!StringUtils.isEmpty(paramVo.getOrder())){
            String[] split = paramVo.getOrder().split(":");
            String orderField;
            switch (split[0]){
                case "1":
                    orderField = "hotScore";break;
                case "2": orderField = "price";break;
                default:  orderField =  "hotScore";
            }
            Sort sort = Sort.by(orderField);
            if (split[0].equals("asc")){
                sort = sort.ascending();
            }else {
                sort = sort.descending();
            }
            query.addSort(sort);
        }

        //分页
        PageRequest pageRequest = PageRequest.of(paramVo.getPageNo() - 1, SysRedisConst.SEARCH_PAGE_SIZE);
        query.setPageable(pageRequest);

        //高亮
        if (!StringUtils.isEmpty(paramVo.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("title")
                    .preTags("<span style='color:red'>")
                    .postTags("</span>");
            HighlightQuery highlightQuery = new HighlightQuery(highlightBuilder);
            query.setHighlightQuery(highlightQuery);
        }

        //=========聚合分析上面DSL检索到的所有商品涉及了多少种品牌和多少种平台属性
        //TODO
        //品牌聚合
        TermsAggregationBuilder tmIdAgg = AggregationBuilders.terms("tmIdAgg").field("tmId").size(1000);
        //子聚合
        TermsAggregationBuilder tmNameAgg = AggregationBuilders.terms("tmNameAgg").field("tmName").size(1);
        TermsAggregationBuilder tmLogoAgg = AggregationBuilders.terms("tmLogoAgg").field("tmLogoUrl").size(1);

        tmIdAgg.subAggregation(tmNameAgg);
        tmIdAgg.subAggregation(tmLogoAgg);

        query.addAggregation(tmIdAgg);

        //平台属性聚合
        NestedAggregationBuilder nested = AggregationBuilders.nested("attrAgg", "attrs");

        //平台属性子聚合
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId").size(100);

        TermsAggregationBuilder attrNameAgg = AggregationBuilders.terms("attrNameAgg").field("attrs.attrName").size(1);
        TermsAggregationBuilder attrValueAgg = AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue").size(100);

        attrIdAgg.subAggregation(attrNameAgg);
        attrIdAgg.subAggregation(attrValueAgg);

        nested.subAggregation(attrIdAgg);
        //添加整个属性的聚合条件
        query.addAggregation(nested);

        return query;
    }

}
