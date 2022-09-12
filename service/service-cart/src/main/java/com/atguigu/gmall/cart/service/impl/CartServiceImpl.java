package com.atguigu.gmall.cart.service.impl;
import java.math.BigDecimal;

import com.atguigu.gmall.common.config.threadpool.AppThreadPoolAutoConfiguration;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.Auth.AuthUtils;
import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.feign.product.SkuProductFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.user.UserAuthInfo;
import com.netflix.hystrix.HystrixEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @Author: LAZY
 * @Date: 2022/09/08/2022/9/8
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SkuProductFeignClient skuProductFeignClient;

    @Autowired
    ThreadPoolExecutor executor;


    @Override
    public SkuInfo addToCart(Long skuId, Integer num) {

        //1、决定购物车使用哪个键
        String cartKey = determinCartKey();

        //2、给购物车添加指定商品
        SkuInfo skuInfo = addItemToCart(skuId,num,cartKey);

        //3.超时设置 自动延期 临时用户
        UserAuthInfo currentAuthInfo = AuthUtils.getCurrentAuthInfo();
        if (currentAuthInfo.getUserId() == null){
            //用户未登录状态一直操作临时购物车 每次加入购物车商品 给购物车延期
            redisTemplate.expire(SysRedisConst.CART_KEY+currentAuthInfo.getUserTempId(),90, TimeUnit.DAYS);
        }

        return skuInfo;
    }

    @Override
    public void deleteChecked(String cartKey) {
        //拿到购物车
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);
        //1、拿到选中的商品，并删除。收集所有选中商品的id
        List<String> list = getCheckedItems(cartKey).stream()
                .map(cartInfo -> cartInfo.getSkuId().toString())
                .collect(Collectors.toList());
        //2.删除选中的购物项
        if (list != null && list.size() > 0) {
            cart.delete(list.toArray());
        }

    }


    @Override
    public List<CartInfo> getCheckedItems(String cartKey){
        List<CartInfo> cartList = getCartList(cartKey);
        List<CartInfo> collect = cartList.stream()
                .filter(cartInfo -> cartInfo.getIsChecked() == 1)
                .collect(Collectors.toList());
        return collect;
    }

    /**
     * 更新购物车商品价格 每次用户查询购物车时进行查询
     * @param cartKey
     * @param cartInfos
     */
    @Override
    public void updateCartItemPrice(String cartKey, List<CartInfo> cartInfos) {
        //拿到购物车
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);
        //更新价格
        System.out.println("更新价格启动："+Thread.currentThread()+"---"+Runtime.getRuntime().availableProcessors());
        cartInfos
                .stream()
                .forEach(cartInfo -> {
                    BigDecimal price = skuProductFeignClient.getPrice(cartInfo.getSkuId()).getData();
                    cartInfo.setSkuPrice(price);
                    cartInfo.setUpdateTime(new Date());
                    //更新到购物车
                    cart.put(cartInfo.getSkuId().toString(),Jsons.toStr(cartInfo));
                });
        System.out.println("更新价格结束："+Thread.currentThread());
    }

    /**
     * 将商品加入购物车
     * @param skuId
     * @param num
     * @param cartKey
     * @return
     */
    public SkuInfo addItemToCart(Long skuId, Integer num, String cartKey) {
        //拿到购物车
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);
        //第一次添加商品
        //获取购物车购物项数量
        Long size = cart.size();
        Boolean hasKey = cart.hasKey(skuId.toString());
        if (!hasKey){
            if (size + num >200){
                //超过200购物项 提示
                throw new GmallException(ResultCodeEnum.CART_OVERFLOW);
            }
            //获取商品信息
            SkuInfo skuInfo = skuProductFeignClient.getSkuInfo(skuId).getData();
            //转为购物车中要保存的数据模型
            CartInfo cartInfo = convertSkuInfo2CartInfo(skuInfo);
            //设置好数量
            cartInfo.setSkuNum(num);
            //保存到redis
            cart.put(skuId.toString(), Jsons.toStr(cartInfo));
            return skuInfo;
        }else {
            //非第一次
            //查询实时价格
            BigDecimal price = skuProductFeignClient.getPrice(skuId).getData();

            //查询购物车内的商品信息
            CartInfo cartInfo = getItemFromCart(cartKey,skuId);
            //修改信息
            cartInfo.setCartPrice(price);
            cartInfo.setSkuNum(cartInfo.getSkuNum()+num);
            cartInfo.setCreateTime(new Date());
            //同步到redis
            cart.put(skuId.toString(),Jsons.toStr(cartInfo));

            //将cartInfo转为skuInfo
            SkuInfo skuInfo = convertCartInfo2SkuInfo(cartInfo);
            return skuInfo;
        }

    }
    @Override
    public SkuInfo convertCartInfo2SkuInfo(CartInfo cartInfo) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSkuName(cartInfo.getSkuName());
        skuInfo.setSkuDefaultImg(cartInfo.getImgUrl());
        skuInfo.setId(cartInfo.getSkuId());
        return skuInfo;
    }


    @Override
    public CartInfo getItemFromCart(String cartKey, Long skuId) {
        //拿到购物车
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);
        String cartInfoJson = cart.get(skuId.toString());
        return Jsons.toObj(cartInfoJson,CartInfo.class);
    }

    @Override
    public List<CartInfo> getCartList(String cartKey) {
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> list = cart.values().stream()
                .map(cartJson -> Jsons.toObj(cartJson, CartInfo.class))
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                .collect(Collectors.toList());

        //更新购物车商品的价格
        //1.老请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //【异步会导致feign丢失请求】
        executor.submit(()-> {
            //2.绑定请求到到这个线程
            RequestContextHolder.setRequestAttributes(requestAttributes);
            updateCartItemPrice(cartKey,list);
            //3.移除数据
            RequestContextHolder.resetRequestAttributes();
        });
        return list;
    }

    /**
     * 修改商品数量
     * @param skuId
     * @param num
     * @param cartKey
     */
    @Override
    public void updateItemNum(Long skuId, Integer num, String cartKey) {
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);
        String cartJson = cart.get(skuId.toString());
        CartInfo cartInfo = Jsons.toObj(cartJson, CartInfo.class);
        cartInfo.setSkuNum(cartInfo.getSkuNum()+num);
        cart.put(skuId.toString(),Jsons.toStr(cartInfo));
    }

    /**
     * 修改商品选中状态
     * @param skuId
     * @param status
     * @param cartKey
     */
    @Override
    public void updateCheckStatus(Long skuId, Integer status, String cartKey) {
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);
        String cartJson = cart.get(skuId.toString());
        CartInfo cartInfo = Jsons.toObj(cartJson, CartInfo.class);
        cartInfo.setIsChecked(status);
        cart.put(skuId.toString(),Jsons.toStr(cartInfo));
    }

    /**
     * 删除购物车内的商品
     * @param skuId
     * @param cartKey
     */
    @Override
    public void deleteCartItem(Long skuId, String cartKey) {
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);
        cart.delete(skuId.toString());
    }

    /**
     * 合并购物车
     */
    @Override
    public void mergeUserAndTempCart() {
        UserAuthInfo userAuthInfo = AuthUtils.getCurrentAuthInfo();
        //1、判断是否需要合并 存在用户id以及临时id 可能需要合并
        if (userAuthInfo.getUserId() != null && !StringUtils.isEmpty(userAuthInfo.getUserTempId())){
            //查询临时购物车
            String userTempCartKey = SysRedisConst.CART_KEY+userAuthInfo.getUserTempId();
            List<CartInfo> tempCartList = getCartList(userTempCartKey);
            if (tempCartList.size()>0 && tempCartList != null){
                //将临时购物车合并到用户购物车
                for (CartInfo cartInfo : tempCartList) {
                    //一个一个添加
                    addItemToCart(cartInfo.getSkuId(), cartInfo.getSkuNum(), SysRedisConst.CART_KEY+userAuthInfo.getUserId());
                    //添加一个删除一个
                    deleteCartItem(cartInfo.getSkuId(),userTempCartKey);
                }
            }
        }



    }

    /**
     * 将skuinfo转为cartInfo
     * @param skuInfo
     * @return
     */
    @Override
    public CartInfo convertSkuInfo2CartInfo(SkuInfo skuInfo) {
        CartInfo cartInfo = new CartInfo();
        cartInfo.setSkuId(skuInfo.getId());
        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
        cartInfo.setSkuName(skuInfo.getSkuName());
        cartInfo.setIsChecked(1);
        cartInfo.setCreateTime(new Date());
        cartInfo.setUpdateTime(new Date());
        cartInfo.setSkuPrice(skuInfo.getPrice());
        cartInfo.setCartPrice(skuInfo.getPrice());
        return cartInfo;
    }

    @Override
    public String determinCartKey() {
        UserAuthInfo userAuthInfo = AuthUtils.getCurrentAuthInfo();
        String cartKey = SysRedisConst.CART_KEY;
        if (!StringUtils.isEmpty(userAuthInfo.getUserId())){
            cartKey = cartKey + userAuthInfo.getUserId();
        }else {
            cartKey = cartKey + userAuthInfo.getUserTempId();
        }
        return cartKey;
    }
}
