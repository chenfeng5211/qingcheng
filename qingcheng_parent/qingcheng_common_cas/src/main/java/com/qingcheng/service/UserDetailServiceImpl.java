/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: UserDetailServiceImpl
 * Author:   chenf
 * Date:     2019/8/3 0003 21:14
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈自定义认证类〉
 *
 * @author chenf
 * @create 2019/8/3 0003
 * @since 1.0.0
 */
public class UserDetailServiceImpl implements UserDetailsService {

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            List<GrantedAuthority> authorityList=new ArrayList<GrantedAuthority>();
            authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));

            return new User(username,"",authorityList);
        }

}