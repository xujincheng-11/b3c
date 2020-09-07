package com.mr.service;

import com.mr.bo.UserInfo;
import com.mr.client.GoodClient;
import com.mr.common.utils.JsonUtils;
import com.mr.pojo.Cart;
import com.mr.service.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName CartService
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/29
 * @Version V1.0
 **/
@Service
public class CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GoodClient goodClient;

    //规定redis中的key
    static final String KEY_PREFIX = "b2c:cart:uid:";


    /**
     * 添加购物车/增加
     * 增加购物车商品到redis
     * */

    public void addCart(Cart cart, UserInfo userInfo) {
        // Redis的key,就是用户id
        String key = KEY_PREFIX +userInfo.getId();

        /**
         * Redis作为购物车存储
         * 结构是一个双层Map：Map<String,Map<String,String>>
         * - 第一层Map，Key是用户id
         * - 第二层Map，Key是购物车中商品id，值是购物车数据
         * */
        //通过用户id,获得该用户在redis缓存中的购物车数据,
        //获得绑定key的hash对象(就是key的value值)
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        // 查询是否存在
        Long skuId = cart.getSkuId();
        Integer num = cart.getNum();
        //通过用户id获得用户数据.从用户数据中查询skuid的商品是否存在,进行判断
        Boolean boo = hashOps.hasKey(skuId.toString());
        //是否购买过
        if(boo){
            // 存在,证明购买过，要进行数量新增(第一次点tostring返回的是Object类型,第二次点toString返回的是String类型)
            String json = hashOps.get(skuId.toString()).toString();
            //获得数据进行转换(解析JSON字符串)
            cart = JsonUtils.parse(json, Cart.class);
            // 修改购物车数量
            cart.setNum(cart.getNum()+num);
        }else{
            // 不存在，证明没有购买过 就新增sku信息
            cart.setUserId(userInfo.getId());
            // 前台只传了skuid和购买数量num，其他数据要进行查询
            Sku sku =goodClient.querySkuById(skuId);
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "": StringUtils.split(sku.getImages(),",")[0]) ;
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setPrice(sku.getPrice());
        }
        // 将购物车数据写入redis中(购物车的数据类型要与redis缓存的数据类型一致,才能放入;Map<String,Map<String,String>>)
        hashOps.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
    }

    /**
     * 查询登录后购物车数据
     * */
    public List<Cart> queryCartList(UserInfo info) {
        // 判断用户是否有购物车数据(就是说redis缓存有没有购物车数据)
        String key = KEY_PREFIX + info.getId();
        if(!this.redisTemplate.hasKey(key)){
            // 不存在，直接返回
            return null;
        }
        //获得绑定key的hash对象(就是key的value值)
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        List<Object> cartList = hashOps.values();
        // 判断是否有数据
        //利用单列集合(list,set集合)的工具类判断list集合是否有数据
        if(CollectionUtils.isEmpty(cartList)){
            return null;
        }
        // 查询购物车数据
        //将数据转成流后,遍历集合,通过parse()方法进行数据转换(解析JSON字符串),最后去redis查询购物车数据
        return cartList.stream().map(o -> JsonUtils.parse(o.toString(), Cart.class)).collect(Collectors.toList());
    }

    /**
     *  修改购物车内商品数量
     * */
    public void updateNum(Long skuId, Integer num, UserInfo info) {
        //获得拼接redis缓存key
        String key = KEY_PREFIX + info.getId();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        // 获取购物车某个具体商品数据
        String json = hashOps.get(skuId.toString()).toString();
        //获得数据进行转换
        Cart cart = JsonUtils.parse(json, Cart.class);
        //覆盖商品购买数量
        cart.setNum(num);
        // 将购物车修改的数量写入redis中,最终展示到购物车中
        hashOps.put(skuId.toString(), JsonUtils.serialize(cart));
    }

    /**
     * 删除购物车数据
     * */
    public void deleteCart(String skuId, UserInfo userInfo) {

        //获得拼接redis缓存key(获得当前用户的购物车 hash对象)
        String key = KEY_PREFIX + userInfo.getId();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        //redis缓存中删除sku信息
        hashOps.delete(skuId);
    }
}
