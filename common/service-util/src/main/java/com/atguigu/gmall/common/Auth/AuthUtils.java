package com.atguigu.gmall.common.Auth;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.model.user.UserAuthInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import javax.servlet.http.HttpServletRequest;

/**
 * 利用Tomcat请求与线程绑定机制。+ Spring自己的 RequestContextHolder ThreadLocal原理
 * 同一个请求在处理期间，任何时候都能共享到数据
 * @Author: LAZY
 * @Date: 2022/09/08/2022/9/8
 */
public class AuthUtils {

    public static UserAuthInfo getCurrentAuthInfo(){
        //拿到老的请求
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        //获取信息
        UserAuthInfo userAuthInfo = new UserAuthInfo();
        String header = request.getHeader(SysRedisConst.USERID_HEADER);
        if (!StringUtils.isEmpty(header)){
            userAuthInfo.setUserId(Long.parseLong(header));
        }

        String tempHeader = request.getHeader(SysRedisConst.USERTEMPID_HEADER);
        userAuthInfo.setUserTempId(tempHeader);

        return userAuthInfo;

    }

}
