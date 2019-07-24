/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: UserDetailServiceImpl
 * Author:   chenf
 * Date:     2019/7/22 0022 8:09
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.service.system.AdminService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈登录校验〉
 *
 * @author chenf
 * @create 2019/7/22 0022
 * @since 1.0.0
 */
public class UserDetailServiceImpl implements UserDetailsService {

    @Reference
    private AdminService adminService;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

//        封装登录信息查询用户是否存在
        Map<String, Object> loginMap = new HashMap<String, Object>();
        loginMap.put("loginName", s);
        loginMap.put("status","1");
        List<Admin> loginList = adminService.findList(loginMap);
        if(loginList.size() == 0){
            return null;
        }
//        当前用户具有的角色
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        List<String> resKey = adminService.findResKey(s);
        for (String s1 : resKey) {
            grantedAuthorities.add(new SimpleGrantedAuthority(s1));
        }
        return new User(s, loginList.get(0).getPassword(), grantedAuthorities);
    }
}