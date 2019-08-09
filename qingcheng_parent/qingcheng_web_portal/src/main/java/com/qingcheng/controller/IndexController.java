/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: AdController
 * Author:   chenf
 * Date:     2019/7/25 0025 19:50
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.business.Ad;
import com.qingcheng.service.business.AdService;
import com.qingcheng.service.goods.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * 〈〉
 *
 * @author chenf
 * @create 2019/7/25 0025
 * @since 1.0.0
 */
@Controller
public class IndexController {

    @Reference
    private AdService adService;

    @Reference
    private CategoryService categoryService;

    @RequestMapping("/index")
    public String index(Model model){

//        获取轮播图
        List<Ad> lbtList = adService.findByP("web_index_lb");
        model.addAttribute("lbtList", lbtList);

//        获取首页分类
        List<Map<String, Object>> categoryList = categoryService.findCategoryByIsShow();
        model.addAttribute("categoryList", categoryList);


        return "index";
    }
}