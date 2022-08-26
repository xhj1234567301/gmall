package com.atguigu.gmall.product.service;

import org.springframework.web.multipart.MultipartFile;



/**
 * @Author: LAZY
 * @Date: 2022/08/25/2022/8/25
 */
public interface FileUploadService {
    String fileUpload(MultipartFile multipartFile) throws Exception;
}
