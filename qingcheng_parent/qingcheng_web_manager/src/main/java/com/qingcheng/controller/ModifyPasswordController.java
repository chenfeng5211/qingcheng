/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: ModifyPasswordController
 * Author:   chenf
 * Date:     2019/7/23 0023 0:43
 * Description: 修改用户密码
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.service.system.AdminService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 〈修改用户密码〉
 *
 * @author chenf
 * @create 2019/7/23 0023
 * @since 1.0.0
 */
@RestController
@RequestMapping("/updatePassword")
public class ModifyPasswordController {

    @Reference
    private AdminService adminService;

    @RequestMapping("/update")
    public Result update(@RequestBody Map<String, Object> searchMap){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        adminService.updatePassword(name, searchMap);


        return new Result();
    }
}