package com.atguigu.gmall.product;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.MinioException;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;


/**
 * @Author: LAZY
 * @Date: 2022/08/25/2022/8/25
 */
public class MinioTest {

    @Test
    public void upload() throws Exception{
        try {
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient("http://192.168.6.100:9000", "admin", "admin123456");

            // 检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists("gmall");
            if(isExist) {
                System.out.println("Bucket already exists.");
            } else {
                // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
                minioClient.makeBucket("asiatrip");
            }
            FileInputStream fileInputStream = new FileInputStream("D:\\atguigu\\10-尚品汇\\资料\\资料\\03 商品图片\\品牌\\oppo.png");
            PutObjectOptions objectOptions = new PutObjectOptions(fileInputStream.available(), -1);
            objectOptions.setContentType("image/png");
            // 使用putObject上传一个文件到存储桶中。
            minioClient.putObject("gmall","oppo.png",
                    fileInputStream,
                    objectOptions
                   );
            System.out.println("文件上传成功");
        } catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }
}
