package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.model.user.UserInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: LAZY
 * @Date: 2022/09/06/2022/9/6
 */
@Controller
public class LoginController {

    @GetMapping("login.html")
    public String login(@RequestParam("originUrl") String originUrl,
                        Model model){

        model.addAttribute("originUrl",originUrl);
        return "login";

    }
}
