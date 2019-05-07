package com.ego.cart.service;

import com.ego.auth.entity.UserInfo;
import com.ego.cart.client.GoodsClient;
import com.ego.cart.interceptor.AuthInterceptor;
import com.ego.cart.pojo.Cart;
import com.ego.common.utils.JsonUtils;
import com.ego.item.pojo.Sku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/21
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Service
public class CartService {
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final static String  KEY_PRE="ego:cart:user:";


    public void addCart(Cart cart) {
        //用户信息？
        UserInfo userInfo = AuthInterceptor.getUser();

        BoundHashOperations<String, Object, Object> carts = stringRedisTemplate.boundHashOps(KEY_PRE + userInfo.getId());

        String skuId = cart.getSkuId().toString();
        Integer num = cart.getNum();
        //判断是否已经存在该购物项
        if (carts.hasKey(skuId)) {
            //存在-->num累加
            String json = carts.get(skuId).toString();
            //json-->对象
            cart = JsonUtils.parse(json, Cart.class);
            cart.setNum(cart.getNum() + num);
        } else {
            //不存在-->新增购物项
            cart = new Cart();
            cart.setNum(num);
            //通过商品微服务查询sku内容
            Sku sku = goodsClient.querySkuById(skuId);
            cart.setTitle(sku.getTitle());
            cart.setImage(sku.getImages());
            cart.setPrice(sku.getPrice());
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setSkuId(sku.getId());
            cart.setUserId(userInfo.getId());

        }

        //将cart写入redis
        carts.put(skuId, JsonUtils.serialize(cart));


    }

    /**
     * 查询当前用户的购物车列表
     *
     * @return
     */
    public List<Cart> list() {
        BoundHashOperations<String, Object, Object> carts = stringRedisTemplate.boundHashOps(KEY_PRE + AuthInterceptor.getUser().getId());
        //将购物车中的所有的购物项(json)-->cart-->放入list<Cart>
        return carts.values().stream().map(json->JsonUtils.parse(json.toString(),Cart.class)).collect(Collectors.toList());
    }

    public void updateNum(Cart cart) {
        BoundHashOperations<String, Object, Object> carts = stringRedisTemplate.boundHashOps(KEY_PRE + AuthInterceptor.getUser().getId());

        Integer num = cart.getNum();

        String json = (String) carts.get(cart.getSkuId().toString());
        cart = JsonUtils.parse(json, Cart.class);
        cart.setNum(num);

        //写入到redis
        carts.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
    }

    public void deleteCart(String skuId) {
        BoundHashOperations<String, Object, Object> carts = stringRedisTemplate.boundHashOps(KEY_PRE + AuthInterceptor.getUser().getId());
        carts.delete(skuId);
    }
}
