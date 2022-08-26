package com.atguigu.gmall.web.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/08/26/2022/8/26
 */
@RequestMapping("/api/inner/rpc/product")
@FeignClient("service-product")
public interface CategoryFeignClient {

    @GetMapping("/category/tree")
    Result<List<CategoryTreeTo>> getCategoryTree();
}
