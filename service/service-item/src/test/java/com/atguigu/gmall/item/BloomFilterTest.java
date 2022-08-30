package com.atguigu.gmall.item;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.jupiter.api.Test;

/**
 * @Author: LAZY
 * @Date: 2022/08/30/2022/8/30
 */
public class BloomFilterTest {

    @Test
    void bloomTest(){
            //1.创建布隆过滤器
        BloomFilter<Long> filter = BloomFilter.create(Funnels.longFunnel(), 10000, 0.00001);

        //2.添数据
        for (long i = 0; i < 20; i++) {
            filter.put(i);
        }
        //3.判断是否存在
        System.out.println("filter.mightContain(1L) = " + filter.mightContain(1L));
        System.out.println("filter.mightContain(15L) = " + filter.mightContain(15L));
        System.out.println("filter.mightContain(20L) = " + filter.mightContain(20L));


    }
}
