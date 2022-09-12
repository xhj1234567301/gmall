package com.atguigu.gmall.common.config;

import com.atguigu.gmall.common.constant.SysRedisConst;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: LAZY
 * @Date: 2022/09/11/2022/9/11
 */
@Configuration
public class FeignInterceptorConfiguration {

    /**
     * 把用户id带到feign即将发起的新请求中
     * @return
     */
    @Bean
    public RequestInterceptor requestInterceptor(){
        return (template)->{
            //修改请求模板
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            //用户id头添加到feign的新请求中
            String userId = request.getHeader(SysRedisConst.USERID_HEADER);
            template.header(SysRedisConst.USERID_HEADER,userId);

            //临时id也透传
            String tempId = request.getHeader(SysRedisConst.USERTEMPID_HEADER);
            template.header(SysRedisConst.USERTEMPID_HEADER,tempId);
        };
    }
}
