package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.vo.SearchParamVo;
import com.atguigu.gmall.model.vo.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: LAZY
 * @Date: 2022/09/05/2022/9/5
 */
@Controller
public class SearchController {

    @Autowired
    SearchFeignClient searchFeignClient;

    @GetMapping("/list.html")
    public String search(SearchParamVo searchParamVo, Model model, HttpServletRequest request){
        Result<SearchResponseVo> search = searchFeignClient.search(searchParamVo);
        SearchResponseVo data = search.getData();

        //页面要显示的数据
        //1、以前检索页面点击传来的所有条件，原封不动返回给页面
        model.addAttribute("searchParam",data.getSearchParam());
        //2、品牌面包屑位置的显示
        model.addAttribute("trademarkParam",data.getTrademarkParam());
        //3、属性面包屑，是集合。集合里面每个元素是一个对象，拥有这些数据（attrName、attrValue、attrId）
        model.addAttribute("propsParamList",data.getPropsParamList());
        //4、所有品牌，是集合。集合里面每个元素是一个对象，拥有这些数据（tmId、tmLogoUrl、tmName）
        model.addAttribute("trademarkList",data.getTrademarkList());
        //5、所有属性，是集合。集合里面每个元素是一个对象，拥有这些数据（attrId，attrName，List<String> attrValueList， ）
        model.addAttribute("attrsList",data.getAttrsList());
        //6、排序信息。是对象。 拥有这些数据（type，sort）
        model.addAttribute("orderMap",data.getOrderMap());
        //7、所有商品列表。是集合。集合里面每个元素是一个对象,拥有这些数据(es中每个商品的详细数据)
        model.addAttribute("goodsList",data.getGoodsList());
        //8、分页信息
        model.addAttribute("pageNo",data.getPageNo());
        model.addAttribute("totalPages",data.getTotalPages());
        //9、url信息
        model.addAttribute("urlParam",data.getUrlParam());

        return "list/index";
    }
}
