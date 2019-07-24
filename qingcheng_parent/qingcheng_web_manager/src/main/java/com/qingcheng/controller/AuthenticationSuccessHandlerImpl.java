/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: AuthenticationSuccessHandlerImpl
 * Author:   chenf
 * Date:     2019/7/22 0022 21:34
 * Description: spring提供的登录成功处理器
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.system.LoginLog;
import com.qingcheng.service.system.LoginLogService;
import com.qingcheng.utils.WebUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * 〈spring提供的登录成功处理器〉
 *
 * @author chenf
 * @create 2019/7/22 0022
 * @since 1.0.0
 */
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Reference
    private LoginLogService loginLogService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {


        String header = httpServletRequest.getHeader("user-agent");
        String ip = httpServletRequest.getRemoteAddr();

        LoginLog loginLog = new LoginLog();
        loginLog.setLoginName(authentication.getName());
        loginLog.setIp(ip);
        loginLog.setBrowserName(WebUtil.getBrowserName(header));
        loginLog.setLocation(WebUtil.getCityByIP(httpServletRequest.getRemoteAddr()));
        loginLog.setLoginTime(new Date());

        loginLogService.add(loginLog);

        httpServletRequest.getRequestDispatcher("/main.html").forward(httpServletRequest, httpServletResponse);
    }
}