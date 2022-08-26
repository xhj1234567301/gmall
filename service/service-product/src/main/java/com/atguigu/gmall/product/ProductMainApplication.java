package com.atguigu.gmall.product;

import com.atguigu.gmall.common.config.Swagger2Config;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author: LAZY
 * @Date: 2022/08/22/2022/8/22
 */
@MapperScan("com.atguigu.gmall.product.mapper")
@SpringCloudApplication
@EnableSwagger2
@Import({Swagger2Config.class})
public class ProductMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductMainApplication.class,args);
    }
}
