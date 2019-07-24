/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: LoginController
 * Author:   chenf
 * Date:     2019/7/22 0022 21:06
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈获取当前登录用户〉
 *
 * @author chenf
 * @create 2019/7/22 0022
 * @since 1.0.0
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/findUser")
    public Map<String, String> findUser(){
        Map<String, String> userList = new HashMap<String, String>();

//        获取上下文环境SecurityContextHolder.getContext()， 在获取认证对象
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        userList.put("loginName", name);

        return userList;
    }


}