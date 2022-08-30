package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @Author: LAZY
 * @Date: 2022/08/29/2022/8/29
 */
@SpringBootTest
public class RedisTest {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void test1(){
        stringRedisTemplate.opsForValue().set("key","111");
        System.out.println("stringRedisTemplate.opsForValue().get(\"key\") = " + stringRedisTemplate.opsForValue().get("key"));
    }


}
