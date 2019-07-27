/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: Init
 * Author:   chenf
 * Date:     2019/7/26 0026 20:46
 * Description: 服务器启动将广告加载到redis中
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.service.impl;

import com.qingcheng.service.business.AdService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 〈服务器启动将广告加载到redis中〉
 *
 * @author chenf
 * @create 2019/7/26 0026
 * @since 1.0.0
 */
@Component
public class Init implements InitializingBean {

    @Autowired
    private AdService adService;

    public void afterPropertiesSet() throws Exception {
        adService.saveAdAllToRedis();
    }
}