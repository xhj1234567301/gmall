package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.LoginSuccessVo;
import com.atguigu.gmall.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: LAZY
 * @Date: 2022/09/06/2022/9/6
 */
@RequestMapping("/api/user")
@RestController
public class UserController {

    @Autowired
    UserInfoService userInfoService;

    /**
     * 登录
     * @param userInfo
     * @return
     */
    @PostMapping("/passport/login")
    public Result login(@RequestBody UserInfo userInfo){

        LoginSuccessVo vo = userInfoService.login(userInfo);
        if (vo != null){
            return Result.ok(vo);
        }
        return Result.build(null, ResultCodeEnum.LOGIN_ERROR);
    }

    /**
     * 退出登录
     * @param token
     * @return
     */
    @GetMapping("/passport/logout")
    public Result logout(@RequestHeader("token") String token){
        userInfoService.logout(token);
        return Result.ok();
    }
}
