package com.atguigu.gmall.model.vo;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import lombok.Data;

import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/09/05/2022/9/5
 */
@Data
public class SearchParamVo {

    Long category3Id;
    Long category1Id;
    Long category2Id;
    String keyword;
    String trademark;

    String[] props;

    String order = "1:desc";
    Integer pageNo = 1;
}
