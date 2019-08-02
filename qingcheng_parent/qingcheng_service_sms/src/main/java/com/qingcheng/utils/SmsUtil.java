/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: SmsUtil
 * Author:   chenf
 * Date:     2019/8/2 0002 1:04
 * Description: 阿里云短信发送工具类
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsUtil {

    @Value("${accessKeyId}")
    private String accessKeyId;

    @Value("${accessKeySecret}")
    private String accessKeySecret;
    public CommonResponse SendSms(String phone, String smsCode, String param) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "青橙电商");
        request.putQueryParameter("TemplateCode", smsCode);
        request.putQueryParameter("TemplateParam", param);
        try {
            CommonResponse response = client.getCommonResponse(request);
            return response;
        } catch (ServerException e) {
            e.printStackTrace();
            return  null;
        } catch (ClientException e) {
            e.printStackTrace();
            return null;
        }
    }
}