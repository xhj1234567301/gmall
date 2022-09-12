package com.atguigu.gmall.cart;

import com.atguigu.gmall.common.annotation.EnableAutoExceptionHandler;
import com.atguigu.gmall.common.annotation.EnableAutoFeignInterceptor;
import com.atguigu.gmall.common.config.annotation.EnableThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: LAZY
 * @Date: 2022/09/07/2022/9/7
 */
@EnableAutoFeignInterceptor
@EnableAutoExceptionHandler //自定义全局异常处理器
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.feign.product"})
@EnableThreadPool
@SpringCloudApplication
public class CartMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartMainApplication.class,args);
    }
}
