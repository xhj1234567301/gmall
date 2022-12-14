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
     * es?????????????????????????????????
     * @param paramVo
     * @return
     */
    @Override
    public SearchResponseVo search(SearchParamVo paramVo) {

        //1??????????????????????????????
        Query query = buildQueryDsl(paramVo);


        //2?????????
        SearchHits<Goods> search = esRestTemplate.search(query,Goods.class, IndexCoordinates.of("goods"));

        //3??????????????????????????????
        SearchResponseVo responseVo = buildSearchResponseResult(search,paramVo);

        return responseVo;
    }

    /**
     * ???????????????
     * @param skuId
     * @param score
     */
    @Override
    public void updateHotScore(Long skuId, Long score) {

        //1.????????????
        Goods goods = goodsRepository.findById(skuId).get();
        //2.???????????????
        goods.setHotScore(score);
        //3.?????????es
        goodsRepository.save(goods);

    }

    /**
     * ?????????????????????????????????????????????
     * @param goods
     * @param paramVo
     * @return
     */
    private SearchResponseVo buildSearchResponseResult(SearchHits<Goods> goods, SearchParamVo paramVo) {
        SearchResponseVo vo = new SearchResponseVo();

        //1??????????????????????????????????????????
        vo.setSearchParam(paramVo);
        //2. ????????????????????? trademark=1:??????
        if (!StringUtils.isEmpty(paramVo.getTrademark())){
            vo.setTrademarkParam("?????????" + paramVo.getTrademark().split(":")[1]);
        }
        //3????????????????????????
        if(paramVo.getProps()!= null && paramVo.getProps().length>0){
            String[] props = paramVo.getProps();
            List<SearchAttr> propsParamList = new ArrayList<>();
            for (String prop : props) {
                String[] split = prop.split(":");
                //??????SearchAttr ???????????????????????????
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(Long.parseLong(split[0]));
                searchAttr.setAttrName(split[1]);
                searchAttr.setAttrValue(split[2]);
                propsParamList.add(searchAttr);
            }
        }

        //TODO 4????????????????????? ?????????ES????????????
        List<TrademarkVo> trademarkVoList = buildTrademarkList(goods);
        vo.setTrademarkList(trademarkVoList);
        //TODO 5????????????????????? ?????????ES????????????
        List<AttrVo> attrVoList = buildAttrList(goods);
        vo.setAttrsList(attrVoList);

        //????????????
        //6?????????????????????  order=1:desc
        if (!StringUtils.isEmpty(paramVo.getOrder())){
            String order = paramVo.getOrder();
            String[] split = order.split(":");
            OrderMapVo orderMapVo = new OrderMapVo();
            orderMapVo.setSort(split[0]);
            orderMapVo.setSort(split[1]);
            vo.setOrderMap(orderMapVo);
        }

        //7?????????????????????????????????
        List<Goods> goodsList = new ArrayList<>();
        List<SearchHit<Goods>> searchHits = goods.getSearchHits();
        for (SearchHit<Goods> searchHit : searchHits) {
            Goods content = searchHit.getContent();
            //??????????????????????????????????????????
            if (!StringUtils.isEmpty(paramVo.getKeyword())){
                String title = searchHit.getHighlightField("title").get(0);
                content.setTitle(title);
            }
            goodsList.add(content);
        }
        vo.setGoodsList(goodsList);

        //8?????????
        vo.setPageNo(paramVo.getPageNo());

        //9???????????????
        long totalHits = goods.getTotalHits();//???????????????
        Long totalPage = totalHits%SysRedisConst.SEARCH_PAGE_SIZE == 0 ?
                totalHits/SysRedisConst.SEARCH_PAGE_SIZE :
                totalHits/SysRedisConst.SEARCH_PAGE_SIZE +1;
        vo.setTotalPages(new Integer(totalPage+""));

        //10?????????????????????   /list.html?category2Id=13
        String url = makeUrlParam(paramVo);
        vo.setUrlParam(url);

        return vo;
    }

    /**
     * ?????????????????? ?????????ES????????????
     * @param goods
     * @return
     */
    private List<TrademarkVo> buildTrademarkList(SearchHits<Goods> goods) {

        List<TrademarkVo> trademarkVoList = new ArrayList<>();
        //?????? tmIdAgg ??????
        ParsedLongTerms tmIdAgg = goods.getAggregations().get("tmIdAgg");
        //????????????id???????????????????????????
        for (Terms.Bucket bucket : tmIdAgg.getBuckets()) {
            TrademarkVo trademarkVo = new TrademarkVo();

            //????????????id
            long id = bucket.getKeyAsNumber().longValue();
            trademarkVo.setTmId(id);

            //??????????????????
            ParsedStringTerms tmName = bucket.getAggregations().get("tmNameAgg");
            String tmNameString = tmName.getBuckets().get(0).getKeyAsString();
            trademarkVo.setTmName(tmNameString);

            //????????????logo
            ParsedStringTerms tmLogoAgg = bucket.getAggregations().get("tmLogoAgg");
            String tmLogoUrl = tmLogoAgg.getBuckets().get(0).getKeyAsString();
            trademarkVo.setTmLogoUrl(tmLogoUrl);

            trademarkVoList.add(trademarkVo);
        }
        return trademarkVoList;
    }

    /**
     * ?????????????????? ?????????ES????????????
     * @param goods
     * @return
     */
    private List<AttrVo> buildAttrList(SearchHits<Goods> goods) {

        List<AttrVo> attrVoList = new ArrayList<>();

        //?????? attrAgg ??????
        ParsedNested attrAgg = goods.getAggregations().get("attrAgg");

        //??????id
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        //3?????????????????????id
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            AttrVo attrVo = new AttrVo();
            //3.1?????????id
            long id = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(id);

            //3.2 ?????????
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);

            //3.3 ?????????
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
     * ???????????????
     * @param paramVo
     * @return
     */
    private String makeUrlParam(SearchParamVo paramVo) {
        // list.html?&k=v
        StrBuilder builder = new StrBuilder("list.html?");
        //1??????????????????????????????
        if (!StringUtils.isEmpty(paramVo.getCategory1Id())){
            builder.append("&category1Id="+paramVo.getCategory1Id());
        }
        if (!StringUtils.isEmpty(paramVo.getCategory2Id())){
            builder.append("&category2Id="+paramVo.getCategory2Id());
        }
        if (!StringUtils.isEmpty(paramVo.getCategory3Id())){
            builder.append("&category3Id="+paramVo.getCategory3Id());
        }
        //2.?????????
        if (!StringUtils.isEmpty(paramVo.getKeyword())){
            builder.append("&keyword="+paramVo.getKeyword());
        }
        //3.??????
        if (!StringUtils.isEmpty(paramVo.getTrademark())){
            builder.append("&trademark="+paramVo.getTrademark());
        }
        //4????????????
        String[] props = paramVo.getProps();
        if (props!=null && props.length > 0) {
            for (String prop : paramVo.getProps()) {
                builder.append("&props="+prop);
            }
        }

        return builder.toString();

    }

    /**
     * ??????paramVo ???????????????????????????
     * @param paramVo
     * @return
     */
    private Query buildQueryDsl(SearchParamVo paramVo) {
        //??????BoolQueryBuilder
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //????????????
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
                
                //??????boolQuery
                BoolQueryBuilder nestedQuery = QueryBuilders.boolQuery();
                nestedQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedQuery.must(QueryBuilders.matchQuery("attrs.attrValue",attrValue));

                // ???????????????
                NestedQueryBuilder attrs = QueryBuilders.nestedQuery("attrs", nestedQuery, ScoreMode.None);
                boolQuery.must(attrs);

            }
        }

        //??????????????????????????????????????????dsl???
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

        //??????
        PageRequest pageRequest = PageRequest.of(paramVo.getPageNo() - 1, SysRedisConst.SEARCH_PAGE_SIZE);
        query.setPageable(pageRequest);

        //??????
        if (!StringUtils.isEmpty(paramVo.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("title")
                    .preTags("<span style='color:red'>")
                    .postTags("</span>");
            HighlightQuery highlightQuery = new HighlightQuery(highlightBuilder);
            query.setHighlightQuery(highlightQuery);
        }

        //=========??????????????????DSL????????????????????????????????????????????????????????????????????????
        //TODO
        //????????????
        TermsAggregationBuilder tmIdAgg = AggregationBuilders.terms("tmIdAgg").field("tmId").size(1000);
        //?????????
        TermsAggregationBuilder tmNameAgg = AggregationBuilders.terms("tmNameAgg").field("tmName").size(1);
        TermsAggregationBuilder tmLogoAgg = AggregationBuilders.terms("tmLogoAgg").field("tmLogoUrl").size(1);

        tmIdAgg.subAggregation(tmNameAgg);
        tmIdAgg.subAggregation(tmLogoAgg);

        query.addAggregation(tmIdAgg);

        //??????????????????
        NestedAggregationBuilder nested = AggregationBuilders.nested("attrAgg", "attrs");

        //?????????????????????
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId").size(100);

        TermsAggregationBuilder attrNameAgg = AggregationBuilders.terms("attrNameAgg").field("attrs.attrName").size(1);
        TermsAggregationBuilder attrValueAgg = AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue").size(100);

        attrIdAgg.subAggregation(attrNameAgg);
        attrIdAgg.subAggregation(attrValueAgg);

        nested.subAggregation(attrIdAgg);
        //?????????????????????????????????
        query.addAggregation(nested);

        return query;
    }

}
