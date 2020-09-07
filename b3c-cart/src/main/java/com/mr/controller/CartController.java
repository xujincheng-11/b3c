package com.mr.controller;

import com.mr.bo.UserInfo;
import com.mr.common.utils.CookieUtils;
import com.mr.config.JwtConfig;
import com.mr.pojo.Cart;
import com.mr.service.CartService;
import com.mr.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @ClassName CartController
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/29
 * @Version V1.0
 **/
@RestController
public class CartController {
    @Resource
    private CartService cartService;
    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 添加购物车(sku商品数据存放在redis)/增加
     * */
    @PostMapping(value="addCart")
    public ResponseEntity<Void> addCart(@RequestBody Cart cart,
                    @CookieValue(name="B2C_TOKEN") String token
        ){
        try {
            //根据获取的token通过公钥解密(id,username),获得登陆用户数据
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            //增加购物车到缓存
            cartService.addCart(cart,userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(null);
    }

    /**
     * 查询登录后购物车数据
     * */
    @GetMapping("queryCartList")
    public ResponseEntity<List<Cart>> queryCartList(@CookieValue("B2C_TOKEN") String token){
        //根据获取的token通过公钥解密(id,username),获得登陆用户信息
        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            //根据用户信息查询购物车数据
            List<Cart> cartList = this.cartService.queryCartList(info);

            //如果无购物车数据则返回无数据状态
            if(cartList==null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            //购物车有数据就返回
            return ResponseEntity.ok(cartList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    /**
     *  修改购物车内商品数量
     * */
    @PostMapping(value="updateNum")
    public ResponseEntity<Void> updateNum(
            @RequestParam("skuId") Long skuId,
            @RequestParam("num") Integer num,
            @CookieValue("B2C_TOKEN") String token
    ){
        try {
            //根据获取的token通过公钥解密(id,username),获得登陆用户信息
            UserInfo info= JwtUtils.getInfoFromToken(token,jwtConfig.getPublicKey());
            //修改购买数量
            this.cartService.updateNum(skuId, num,info);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * 删除购物车数据
     * */
    @DeleteMapping("deleteCart/{skuId}")
    public ResponseEntity<Void> deleteCart(
            @PathVariable("skuId") String skuId,
            HttpServletRequest request
    ) {
        try {
            //从cookie中获得token值
            String token= CookieUtils.getCookieValue(request,jwtConfig.getCookieName());
            //根据获取的token通过公钥解密(id,username),获得登陆用户信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            //删除购物车内某条数sku数据
            this.cartService.deleteCart(skuId,userInfo);

            return ResponseEntity.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
