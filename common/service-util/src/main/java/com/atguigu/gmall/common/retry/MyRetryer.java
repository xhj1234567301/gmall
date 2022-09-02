package com.atguigu.gmall.common.retry;

import feign.RetryableException;
import feign.Retryer;

/**
 * 自定义feign重试次数逻辑
 * @Author: LAZY
 * @Date: 2022/09/02/2022/9/2
 */
public class MyRetryer implements Retryer {

    private int cur = 0;
    private int max = 5;

    public MyRetryer() {
        cur = 0;
        max = 2;
    }

    /**
     * 继续重试还是中断重试
     * @param e
     */
    @Override
    public void continueOrPropagate(RetryableException e) {
        throw e;
    }

    @Override
    public Retryer clone() {
        return this;
    }
}
