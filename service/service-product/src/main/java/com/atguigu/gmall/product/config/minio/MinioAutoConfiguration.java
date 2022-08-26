package com.atguigu.gmall.product.config.minio;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;

/**
 * @Author: LAZY
 * @Date: 2022/08/25/2022/8/25
 */
@Configuration //注册到容器中
public class MinioAutoConfiguration {
    @Autowired
    MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() throws Exception {
        // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
        MinioClient minioClient = new MinioClient(minioProperties.getEndpoint(),
                minioProperties.getAccessKey(),
                minioProperties.getSecretKey());

        // 检查存储桶是否已经存在
        boolean isExist = minioClient.bucketExists(minioProperties.getBucketName());
        if(isExist) {
            System.out.println("Bucket already exists.");
        } else {
            // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
            minioClient.makeBucket(minioProperties.getBucketName());
        }
        return minioClient;
    }
}
