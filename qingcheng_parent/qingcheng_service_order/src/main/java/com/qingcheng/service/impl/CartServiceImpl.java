/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CartServiceImpl
 * Author:   chenf
 * Date:     2019/8/6 0006 19:40
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.pojo.goods.Category;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.order.CartService;
import com.qingcheng.service.order.PreferentialService;
import com.qingcheng.util.CacheKey;
import com.qingcheng.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 〈〉
 *
 * @author chenf
 * @create 2019/8/6 0006
 * @since 1.0.0
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PreferentialService preferentialService;

    @Reference
    private SkuService skuService;

    @Reference
    private CategoryService categoryService;


    /**
     * 功能描述:
     *
     * 获取购物车商品
     * @param username 用户名称
     */

    @Override
    public List<Map<String, Object>> findCart(String username) {

//        从redis中获取购物车中的商品数据,
        List<Map<String, Object>> cartList = (List<Map<String, Object>>) redisTemplate.boundHashOps(CacheKey.CART).get(username);

//        判断购物车中有无商品没有创建一个空的list集合
        if (cartList == null){
            cartList= new ArrayList<Map<String, Object>>();
        }
        System.out.println("进入购物车");
        return cartList;
    }

    /**
     * 向购物车添加修改删除商品
     * @param username 当前购物车所属用户
     * @param skuId 商品详情
     * @param num 商品数量
     * @return
     */
    @Override
    public void addItemToCart(String username, String skuId, Integer num) {

//        1.获取购物车中的所有商品
        List<Map<String, Object>> cartList = findCart(username);

//        定义标记，判断当前商品是否已在购物车中,默认不在
        Boolean flag = false;
//        遍历每个商品
        for (Map<String, Object> cartMap : cartList) {

//            获取itemOrder对象
            OrderItem item = (OrderItem) cartMap.get("item");

//        2.当购物车已有该商品操作
            if(skuId.equals(item.getSkuId())){

                Integer orderNum = item.getNum(); //原来的购物车商品数量
                Integer newNum = orderNum + num;
                item.setNum(newNum); //更新商品数量
                if (newNum <= 0){
                    cartList.remove(cartMap);
                    break;
                }
                item.setWeight(item.getWeight() / orderNum * newNum); //更新该商品总重量
                item.setMoney(item.getMoney() / orderNum * newNum); //更新商品总价格

                cartMap.put("item", item);
                cartMap.put("checked", true);
                flag = true;
                break;
            }
        }
//        3.当购物车没有该该商品操作
        if(!flag){

//            获取当前的商品
            Sku sku = skuService.findById(skuId);

            if(sku == null){
                throw new RuntimeException("无该商品");
            }

            if(!"1".equals(sku.getStatus())){
                throw new RuntimeException("该商品以下架或删除");
            }

            if(num <=0){
                throw new RuntimeException("至少添加一个商品");
            }

            OrderItem orderItem = new OrderItem();

//            分类
            orderItem.setCategoryId3(sku.getCategoryId()); //三级

//            二级
            Category category3 = (Category) redisTemplate.boundHashOps(CacheKey.CATEGORY).get(sku.getCategoryId());
            if(category3 == null){
                category3 = categoryService.findById(sku.getCategoryId());
                redisTemplate.boundHashOps(CacheKey.CATEGORY).put(sku.getCategoryId(), category3);
            }
            orderItem.setCategoryId2(category3.getParentId());

//            一级
            Category category2 = (Category) redisTemplate.boundHashOps(CacheKey.CATEGORY).get(category3.getParentId());
            if(category2 == null){
                category2 = categoryService.findById(category3.getParentId());
                redisTemplate.boundHashOps(CacheKey.CATEGORY).put(category3.getParentId(), category2);
            }
            orderItem.setCategoryId1(category2.getParentId());


//            orderItem.setId(idWorker.nextId()+"");
            orderItem.setSpuId(sku.getSpuId());
            orderItem.setSkuId(sku.getId());
//            orderItem.setOrderId(idWorker.nextId()+"");
            orderItem.setName(sku.getName());
            orderItem.setPrice(sku.getPrice());
            orderItem.setNum(num);
            orderItem.setMoney(sku.getPrice() * num);
            orderItem.setImage(sku.getImage());
            if(sku.getWeight()<0){
                sku.setWeight(0);
            }
            orderItem.setWeight(sku.getWeight() * num);

            Map<String, Object> cartMap = new HashMap<>();
            cartMap.put("item", orderItem);
            cartMap.put("checked", true);

            cartList.add(cartMap);
        }

        redisTemplate.boundHashOps(CacheKey.CART).put(username, cartList);
    }

    /**
     * 修改是否选中
     * @param username 当前用户
     * @param skuId 当前商品id
     * @param checked true  false
     */
    @Override
    public void updateChecked(String username, String skuId, boolean checked) {
//        1.获取所有购物车商品并循环遍历
        List<Map<String, Object>> cartList = (List<Map<String, Object>>) redisTemplate.boundHashOps(CacheKey.CART).get(username);

        boolean flag = false;
        if(cartList != null) {
            for (Map<String, Object> cartMap : cartList) {
                OrderItem item = (OrderItem) cartMap.get("item");
                if(skuId.equals(item.getSkuId())){
//                      2.判断状态
                    if(!cartMap.get("checked").equals(checked)){
                        cartMap.put("checked", checked);
                        flag = true;
                        break;
                    }
                }
            }
        }

//        3.重新存入缓存
        if(flag) {
            redisTemplate.boundHashOps(CacheKey.CART).put(username, cartList);
        }

    }

    /**
     * 删除选中商品
     */
    @Override
    public void deleteChecked(String username) {
        List<Map<String, Object>> cartList = (List<Map<String, Object>>) redisTemplate.boundHashOps(CacheKey.CART).get(username);


        if(cartList != null) {
            cartList = cartList.stream().filter(cartMap -> !((boolean) cartMap.get("checked"))).collect(Collectors.toList());
            redisTemplate.boundHashOps(CacheKey.CART).put(username, cartList);
        }


    }


    /**
     * 功能描述:
     * 获取商品优惠后的实际金额
     */

    @Override
    public Integer getPreMoney(String username) {


        List<Map<String, Object>> cartList = (List<Map<String, Object>>) redisTemplate.boundHashOps(CacheKey.CART).get(username);

//        1.获取选中的orderItem集合

        assert cartList != null;
        List<OrderItem> orderItemList = cartList.stream().
                filter(cartMap -> ((boolean) cartMap.get("checked"))).
                map(cart -> (OrderItem) cart.get("item")).collect(Collectors.toList());

//        2.以分裂类id为key，金额为value封装map集合
        Map<Integer, IntSummaryStatistics> map =
                orderItemList.stream().
                        collect(Collectors.groupingBy(OrderItem::getCategoryId3, Collectors.summarizingInt(OrderItem::getMoney)));

        int preMoney = 0;
        for (Integer categoryId : map.keySet()) {

//            获取每个分类所有商品金额之和
            int sumMoney = (int) map.get(categoryId).getSum();

//           3.调用优惠方法获取优惠优惠后金额
            preMoney += preferentialService.getPreMoney(categoryId, sumMoney);

            System.out.println("测试优惠金额："+preMoney);
        }


        return preMoney;
    }

    @Override
    public List<Map<String, Object>> findPayOrderItem(String username) {

//        这里有点问题，由于获取的是选中的，价格重新存入缓存中覆盖了未选中的
//        1.从redis中获取选中的商品
        List<Map<String, Object>> cartList =
                findCart(username).stream().filter(cartMap -> ((boolean) cartMap.get("checked"))).collect(Collectors.toList());

//        2.查询数据库，更新价格和总金额
        for (Map<String, Object> cartMap : cartList) {

            OrderItem orderItem = (OrderItem) cartMap.get("item");
            Sku sku = skuService.findById(orderItem.getSkuId());
            orderItem.setPrice(sku.getPrice());
            orderItem.setMoney(sku.getPrice()*orderItem.getNum());

            cartMap.put("item", orderItem);

        }
//        3.重新存入到redis中
        redisTemplate.boundHashOps(CacheKey.CART).put(username, cartList);

        return cartList;
    }

}