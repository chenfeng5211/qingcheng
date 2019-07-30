/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: SearchController
 * Author:   chenf
 * Date:     2019/7/29 0029 19:39
 * Description: 商品搜索
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.goods.BrandService;
import com.qingcheng.service.goods.SearchService;
import com.qingcheng.utils.WebUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 〈商品搜索〉
 *
 * @author chenf
 * @create 2019/7/29 0029
 * @since 1.0.0
 */
@Controller

public class SearchController {

    @Reference
    private SearchService searchService;

    @Reference
    private BrandService brandService;

    @RequestMapping("/search")
    public String goodsSearch(Model model, @RequestParam Map<String, String> searchMap, HttpServletRequest request) throws Exception {

//        字符集处理
        searchMap = WebUtil.convertCharsetToUTF8(searchMap);
        Map resultMap = searchService.goodsSearch(searchMap);

        model.addAttribute("resultMap", resultMap);

//        给商品分类拼接url
        StringBuilder url = new StringBuilder(request.getRequestURI());

        url.append("?");
        for (String key : searchMap.keySet()) {
            url.append("&").append(key).append("=").append(searchMap.get(key));
        }
        model.addAttribute("url", url);

        model.addAttribute("searchMap", searchMap);


        return "search";
    }
}