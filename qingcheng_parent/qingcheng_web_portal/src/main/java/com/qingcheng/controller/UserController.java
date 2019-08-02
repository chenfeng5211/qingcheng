/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: UserController
 * Author:   chenf
 * Date:     2019/8/1 0001 19:55
 * Description: 用户接口
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.user.User;
import com.qingcheng.service.user.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 〈用户接口〉
 *
 * @author chenf
 * @create 2019/8/1 0001
 * @since 1.0.0
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;


    /**
     * 功能描述:
     *
     * 用户请求发送验证码
     * @Date: 2019/8/1 0001 20:02
     */

    @RequestMapping("/sendSms")
    public Result sendSms(String phone){

        userService.sendSms(phone);

        return new Result();
    }


    /**
     * 功能描述:
     *
     * 用户注册
     * @Date: 2019/8/1 0001 20:39
     */

    @RequestMapping("/register")
    public Result registerUser(@RequestBody User user, @RequestParam String code){

        userService.addUser(user, code);

        return new Result();
    }
}