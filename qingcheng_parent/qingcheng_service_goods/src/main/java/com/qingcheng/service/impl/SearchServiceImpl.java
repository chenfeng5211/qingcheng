/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: SearchServiceImpl
 * Author:   chenf
 * Date:     2019/7/29 0029 19:04
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.BrandMapper;
import com.qingcheng.dao.SpecMapper;
import com.qingcheng.service.goods.SearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈〉
 *
 * @author chenf
 * @create 2019/7/29 0029
 * @since 1.0.0
 */
@Service
public class SearchServiceImpl implements SearchService {


    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SpecMapper specMapper;

    /**
     * 功能描述:
     * <p>
     * 搜索页面处理
     */

    public Map goodsSearch(Map<String, String> searchMap) {


//        1.封装查询请求
        SearchRequest searchRequest = new SearchRequest("sku");
        searchRequest.types("doc"); //设置查询的类型
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();//布尔查询构建器

//        1.1封装查询索引
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if (searchMap.get("keyword") == null) {

            return (Map) new HashMap().put("name", "");
        }
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", searchMap.get("keyword"));
        boolQueryBuilder.must(matchQueryBuilder);

//        1.2分类过滤
        if (searchMap.get("category") != null) {
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("categoryName", searchMap.get("category"));
            boolQueryBuilder.filter(termQueryBuilder);

        }

//        1.4品牌过滤
        if(searchMap.get("brand") != null) {
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("brandName", searchMap.get("brand"));
            boolQueryBuilder.filter(termQueryBuilder);
        }
//        规格过滤
        if(searchMap.get("spec") != null) {
            for (String spec : searchMap.keySet()) {
                if(spec.startsWith("spec.")){
//                    由于spec在索引库text域，防止在被分词查询所有加上keyword
                    TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(spec+".keyword", searchMap.get(spec));
                    boolQueryBuilder.filter(termQueryBuilder);
                }
            }

        }

        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
//        1.3分类分组
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("sku_category").field("categoryName");
        searchSourceBuilder.aggregation(termsAggregationBuilder);


//        1.4价格查询封装
//        gte:大于等与，lte:小于等于
//        0-500   1000-2000  3000-*
        if(searchMap.get("price")!=null) {
            String[] prices = searchMap.get("price").split("-");
            if(!"0".equals(prices[0])) {
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price").gte(prices[0]+"00");
                boolQueryBuilder.filter(rangeQueryBuilder);

            }
            if(!"*".equals(prices[1])){
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price").lte(prices[1]+"00");
                boolQueryBuilder.filter(rangeQueryBuilder);
            }

        }


        HashMap<String, Object> resultMap = new HashMap<String, Object>();

        try {

//            2封装查询结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
//            2.1搜索结果
            long totalHits = searchHits.getTotalHits();
            SearchHit[] hits = searchHits.getHits();

            List<Map> mapList = new ArrayList<Map>();
            for (SearchHit hit : hits) {
                Map<String, Object> keywordMap = hit.getSourceAsMap();
                mapList.add(keywordMap);
            }
            resultMap.put("rows", mapList);


//            2.2分类聚合查询
            Aggregations aggregations = searchResponse.getAggregations();
            Map<String, Aggregation> aggregationMap = aggregations.getAsMap();

            Terms terms = (Terms) aggregationMap.get("sku_category");

            List<String> categoryList = new ArrayList<String>();
            List<? extends Terms.Bucket> buckets = terms.getBuckets();

            for (Terms.Bucket bucket : buckets) {
                categoryList.add(bucket.getKeyAsString());
            }
            resultMap.put("categoryList", categoryList);


//            组装分类条件
            String categoryName = "";
            if (searchMap.get("category") == null) {
//                没有分类以第一个分类为准
                if (categoryList.size() > 0) {
                    categoryName = categoryList.get(0);
                }
            } else {
//                有分类以当前分类展示
                categoryName = searchMap.get("category");
            }
//            2.3品牌结果
            if(searchMap.get("brand") == null) {
                List<Map> brandList = brandMapper.findBrandByCategoryName(categoryName);
                resultMap.put("brandList", brandList);
            }


//            2.4规格封装结果
            if(searchMap.get("spec")== null) {
                List<Map> specList = specMapper.findSpecByCategory(categoryName);

//                重新组组装spec
                for (Map specMap : specList) {
                    String[] options = ((String) specMap.get("options")).split(",");
                    specMap.put("options", options);
                }

                resultMap.put("specList", specList);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        return resultMap;
    }
}