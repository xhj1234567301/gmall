package com.atguigu.gmall.product.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: LAZY
 * @Date: 2022/08/23/2022/8/23
 */
@Configuration
public class MyBatisPlusConfig {

    @Bean//1、把MybatisPlus的插件主体（总插件）放到容器
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        //插件主体
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //加入内部的小插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setOverflow(true);//页码溢出以后，默认就访问最后一页
        //分页插件
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }
}
