/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: SkuPutMessageConsumer
 * Author:   chenf
 * Date:     2019/8/2 0002 12:36
 * Description: 商品上架
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.qingcheng.pojo.goods.Category;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.goods.Spu;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * 〈商品上架〉
 *
 * @author chenf
 * @create 2019/8/2 0002
 * @since 1.0.0
 */
@Service
public class SkuPutMessageConsumer implements MessageListener {

    @Value("${pagePath}")
    private String pagePath;



    @Autowired
    private TemplateEngine templateEngine;

    public void onMessage(Message message) {
        String jsonSku = new String(message.getBody());
        Map<String, Object> itemMap = JSON.parseObject(jsonSku, Map.class);

        JSON skuJson = (JSON) itemMap.get("sku");
        Sku sku = JSON.toJavaObject(skuJson, Sku.class);
        JSON spuJson = (JSON) itemMap.get("spu");
        Spu spu = JSON.toJavaObject(spuJson, Spu.class);
        List<Category> categories = (List<Category>) itemMap.get("categories");

//        封装sku规格和url映射
        Map<String, String> urlMap = new HashMap<String, String>();
//            判断规格是否存在，在页面上表现为虚
        if ("1".equals(sku.getStatus())) {
//                封装集合，以规格为key，url为值存入到集合中，用于页面选择不同规格时页面跳转
            Map<String, List> ss = (Map) JSON.parseObject(sku.getSpec());
            String sk = JSON.toJSONString(ss, SerializerFeature.MapSortField);
            urlMap.put(sk, sku.getId() + ".html");
        }

//        封装数据模型
        Context context = new Context();
//        封装数据
        HashMap dataModel = new HashMap();

//        封装商品的sku名称，spu副标题，sku中的价格
        dataModel.put("sku", sku);
        dataModel.put("spu", spu);
        dataModel.put("price", sku.getPrice());

//            获取规格并转化为json对象
        Map specItems = JSON.parseObject(sku.getSpec());
        dataModel.put("specItems", specItems);
//            获参数列表，并转化json对象
        Map paraItems = JSON.parseObject(spu.getParaItems());
        dataModel.put("paraItems", paraItems);

        dataModel.put("categories", categories);


//            设置图片
        String[] skuStr = sku.getImages().split(",");
        String[] spuStr = spu.getImages().split(",");
        List<String> images = new ArrayList<String>();
        images.addAll(Arrays.asList(spuStr));
        images.addAll(Arrays.asList(skuStr));
        dataModel.put("images", images);

//            展示所有规格
        Map<String, List> specMap = (Map<String, List>) JSON.parse(spu.getSpecItems());


//            Map集合中再嵌套map
        for (String spec : specMap.keySet()) {
//                创建集合，list集合中嵌套map集合
            List<Map> maps = new ArrayList<Map>();
            List<String> list = specMap.get(spec);
            for (String s : list) {
//                    map中键值对
                Map<String, String> sMap = new HashMap<String, String>();
                sMap.put("option", s);
//                    判断当前商品是否被选中是否存在，页面展示为虚的
                if (s.equals(specItems.get(spec))) {
                    sMap.put("checked", "true");
                } else {
                    sMap.put("checked", "false");
                }

//                    页面跳转，获取当前商品的规格，用户选择的其他规格s改变，重新put到集合中，覆盖已存在的规格
                Map<String, String> specm = (Map) JSON.parseObject(sku.getSpec());
                specm.put(spec, s);
//                    给规格查询排序，防止重复查询
                String specStr = JSON.toJSONString(specm, SerializerFeature.MapSortField);
//                    从urlMap集合中以规格为key，获取url值，在put到重新封装的smap
                sMap.put("url", urlMap.get(specStr));
                maps.add(sMap);


            }
            specMap.put(spec, maps);

        }
        dataModel.put("specAll", specMap);


        context.setVariables(dataModel);

//        静态页面生成路经
        File file = new File(pagePath);
//            判断文件夹是否存在
        if (!file.exists()){
            file.mkdirs();
        }
        PrintWriter printWriter = null;

        try {
//                输出的目标文件
            printWriter = new PrintWriter(new File(file, sku.getId() + ".html"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        使用模板引擎生成静态页面
//        template:模板页面  context：数据模型 writer：输出位置
        templateEngine.process("item", context, printWriter);
        printWriter.close();
    }
}