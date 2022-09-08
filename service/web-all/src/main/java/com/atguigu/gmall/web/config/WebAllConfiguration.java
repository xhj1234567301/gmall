package com.atguigu.gmall.web.config;

import com.atguigu.gmall.common.constant.SysRedisConst;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: LAZY
 * @Date: 2022/09/07/2022/9/7
 */
@Configuration
public class WebAllConfiguration {

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
         String userId = request.getHeader(SysRedisConst.USERID_HEADER);
         //用户id头添加到feign的新情求中
         template.header(SysRedisConst.USERID_HEADER,userId);
     };
    }
}
