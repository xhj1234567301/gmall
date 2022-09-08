package com.atguigu.gmall.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/09/07/2022/9/7
 */
@Data
@ConfigurationProperties(prefix = "app.auth")
@Component
public class AuthUrlProperties {

    //无需登录可直接访问的路径
    List<String> noAuthUrl;
    //必须访问才可以访问
    List<String> loginAuthUrl;
    //登录页地址
    String loginPage;
    //拒接访问的地址
    List<String> denyUrl;
}
