package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.web.feign.CategoryFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/08/26/2022/8/26
 */
@Controller
public class IndexController {

    @Autowired
    CategoryFeignClient categoryFeignClient;

    @RequestMapping({"/","/index"})
    public String indexPage(Model model){
        Result<List<CategoryTreeTo>> categoryTree = categoryFeignClient.getCategoryTree();
        if (categoryTree.isOk()){
            List<CategoryTreeTo> data = categoryTree.getData();
            model.addAttribute("list",data);
        }
        return "index/index";
    }
}
