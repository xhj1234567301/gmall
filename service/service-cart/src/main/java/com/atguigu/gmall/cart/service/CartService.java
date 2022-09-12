package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;

import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/09/08/2022/9/8
 */
public interface CartService {


    /**
     * 决定购物车用哪个键 用户id、临时id
     * @return
     */
    String determinCartKey();

    SkuInfo addItemToCart(Long skuId, Integer num, String cartKey);

    /**
     *
     * @param skuInfo
     * @return
     */
    CartInfo convertSkuInfo2CartInfo(SkuInfo skuInfo);

    SkuInfo convertCartInfo2SkuInfo(CartInfo cartInfo);

    /**
     * 查询购物项信息
     * @param cartKey
     * @param skuId
     * @return
     */
    public CartInfo getItemFromCart(String cartKey, Long skuId);

    /**
     * 根据购物车的键 从redis查询购物项
     * @param cartKey
     * @return
     */
    List<CartInfo> getCartList(String cartKey);

    /**
     * 修改商品数量
     * @param skuId
     * @param num
     * @param cartKey
     */
    void updateItemNum(Long skuId, Integer num, String cartKey);

    /**
     * 修改商品选中状态
     * @param skuId
     * @param status
     * @param cartKey
     */
    void updateCheckStatus(Long skuId, Integer status, String cartKey);

    /**
     * 删除购物车内的商品
     * @param skuId
     * @param cartKey
     */
    void deleteCartItem(Long skuId, String cartKey);

    /**
     * 合并购物车
     */
    void mergeUserAndTempCart();

    SkuInfo addToCart(Long skuId, Integer num);

    /**
     * 删除购物车中选中的商品
     * @param cartKey
     */
    void deleteChecked(String cartKey);

    /**
     * 获取选中的商品集合
     * @param cartKey
     * @return
     */
    List<CartInfo> getCheckedItems(String cartKey);

    /**
     * 更新购物车商品的价格
     * @param cartKey
     */
    void updateCartItemPrice(String cartKey, List<CartInfo> cartInfos);
}
