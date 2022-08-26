package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.product.config.minio.MinioProperties;
import com.atguigu.gmall.product.service.FileUploadService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;


/**
 * @Author: LAZY
 * @Date: 2022/08/25/2022/8/25
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    MinioClient minioClient;

    @Autowired
    MinioProperties minioProperties;

    @Override
    public String fileUpload(MultipartFile multipartFile) throws Exception {

        if (!minioClient.bucketExists(minioProperties.getBucketName())){
            minioClient.makeBucket(minioProperties.getBucketName());
        }

        //上传文件
        String name = multipartFile.getName();
        //唯一的文件名
        String dateStr = DateUtil.formatDate(new Date());
        String fileName = UUID.randomUUID().toString().replaceAll("-","")
                + "_" +multipartFile.getOriginalFilename();//原始文件名
        PutObjectOptions options = new PutObjectOptions(multipartFile.getSize(), -1);
        options.setContentType(multipartFile.getContentType());
        minioClient.putObject(minioProperties.getBucketName(),
                dateStr+"/"+fileName,
                multipartFile.getInputStream(),
                options);
        //返回文件上传的路径
        String url = minioProperties.getEndpoint()+"/"+minioProperties.getBucketName()+"/"+dateStr+"/"+fileName;
        return url;
    }
}
