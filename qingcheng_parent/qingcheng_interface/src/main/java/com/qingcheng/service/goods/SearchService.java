/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: SearchService
 * Author:   chenf
 * Date:     2019/7/29 0029 19:02
 * Description: 商品搜索
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.qingcheng.service.goods;

import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈商品搜索〉
 *
 * @author chenf
 * @create 2019/7/29 0029
 * @since 1.0.0
 */
public interface SearchService {

    public Map goodsSearch(Map<String, String> searchMap);
}