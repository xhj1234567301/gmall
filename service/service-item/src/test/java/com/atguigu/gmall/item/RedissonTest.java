package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author: LAZY
 * @Date: 2022/08/30/2022/8/30
 */
@SpringBootTest
public class RedissonTest {

    @Autowired
    RedissonClient redissonClient;

    @Test
    void test1(){
        System.out.println( redissonClient);
    }
}
