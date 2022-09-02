package com.atguigu.gmall.item;

import com.atguigu.gmall.common.config.RedissonAutoConfiguration;
import com.atguigu.gmall.common.config.annotation.EnableThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

/**
 * @Author: LAZY
 * @Date: 2022/08/26/2022/8/26
 */
//@Import({RedissonAutoConfiguration.class})
@EnableThreadPool
@EnableFeignClients
@SpringCloudApplication
public class ItemMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemMainApplication.class,args);
    }
}
