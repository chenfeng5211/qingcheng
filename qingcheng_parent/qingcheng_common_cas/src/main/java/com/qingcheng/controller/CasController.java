/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: LoginController
 * Author:   chenf
 * Date:     2019/8/3 0003 22:37
 * Description: 获取用户登录信息
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
 * 〈获取用户登录信息〉
 *
 * @author chenf
 * @create 2019/8/3 0003
 * @since 1.0.0
 */
@RestController
@RequestMapping("/login")
public class CasController {

    @RequestMapping("/username")
    public Map<String, String> getUsername(){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        if("anonymousUser".equals(name)){
            name="";
        }

        Map<String, String> userMap = new HashMap<String, String>();
        userMap.put("username", name);
        return userMap;
    }
}