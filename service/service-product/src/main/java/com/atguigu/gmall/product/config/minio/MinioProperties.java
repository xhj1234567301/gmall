package com.atguigu.gmall.product.config.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: LAZY
 * @Date: 2022/08/25/2022/8/25
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.minio")
public class MinioProperties {

    String endpoint;
    String accessKey;
    String secretKey;
    String bucketName;
}
