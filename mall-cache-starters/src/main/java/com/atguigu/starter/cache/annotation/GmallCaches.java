package com.atguigu.starter.cache.annotation;

/**
 * @Author: LAZY
 * @Date: 2022/09/01/2022/9/1
 */

import java.lang.annotation.*;

/**
 * 缓存注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface GmallCaches {
    String cacheKey() default ""; //就是cacheKey

    String bloomName() default "";//如果指定了布隆过滤器的名字，就用

    String bloomValue() default "";//指定布隆过滤器如果需要判定的话，用什么表达式计算出的值进行判定

    String lockName() default ""; //传入精确锁就用精确的，否则用全局默认的
}
