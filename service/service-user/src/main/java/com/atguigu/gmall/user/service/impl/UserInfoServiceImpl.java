package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.LoginSuccessVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.user.service.UserInfoService;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
* @author 懒羊
* @description 针对表【user_info(用户表)】的数据库操作Service实现
* @createDate 2022-09-06 23:14:47
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService{

    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public LoginSuccessVo login(UserInfo userInfo) {
        LoginSuccessVo vo = new LoginSuccessVo();
        //查数据库
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getLoginName,userInfo.getLoginName())
                .eq(UserInfo::getPasswd, MD5.encrypt(userInfo.getPasswd()));
        UserInfo user = userInfoMapper.selectOne(wrapper);
        if (user != null){
            //查询成功
            String token =  UUID.randomUUID().toString().replace("-", "");
            redisTemplate.opsForValue().set(SysRedisConst.LOGIN_USER+token, Jsons.toStr(user),7, TimeUnit.DAYS);
            vo.setToken(token);
            vo.setNickName(user.getNickName());
            return vo;
        }

        return null;
    }

    @Override
    public void logout(String token) {
        redisTemplate.delete(token);
    }
}




