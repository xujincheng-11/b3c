package com.mr.order.bo;

import lombok.Data;

import java.util.List;

/**
 * 订单业务对象，记录订单的地址，支付类型， 购物的sku集合
 */
@Data
public class OrderBo {
    //地址id
    private Long addressId;
    //支付方式
    private  Integer payMentType;
    //购物车数据
    private List<CartBo> cartList;
}
