package com.atguigu.gmall.common.config.threadpool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 自定义线程池
 * @Author: LAZY
 * @Date: 2022/08/28/2022/8/28
 */
@ConfigurationProperties(prefix = "app.thread-pool")
@Data
public class AppThreadPoolProperties {

    Integer core = 2;
    Integer max = 4;
    Integer queueSize = 200;
    Long keepAliveTime = 300L;

}
