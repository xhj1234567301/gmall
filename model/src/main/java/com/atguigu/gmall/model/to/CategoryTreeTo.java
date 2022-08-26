package com.atguigu.gmall.model.to;

import lombok.Data;

import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/08/26/2022/8/26
 */
@Data
public class CategoryTreeTo {

    public Long categoryId;

    public String categoryName;

    public List<CategoryTreeTo> categoryChild;
}
