/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: StockBackTask
 * Author:   chenf
 * Date:     2019/8/7 0007 19:36
 * Description: 回滚订单定时任务
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.goods.StockBackService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 〈回滚订单定时任务〉
 *
 * @author chenf
 * @create 2019/8/7 0007
 * @since 1.0.0
 */
@Component
public class StockBackTask {

    @Reference
    private StockBackService stockBackService;


    /**
     * 功能描述:
     * 每一小时检车tockback表
     */

    @Scheduled(cron = "0 * * * * ?")
    public void setStockBack(){
        System.out.println("执行回滚任务");
        stockBackService.setStockBack();
    }
}