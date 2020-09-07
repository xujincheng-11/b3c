package com.mr.order.bo;

import com.mr.service.pojo.Sku;
import lombok.Data;

/**
 * 购物车商品对象，只记录id和购买数量
 */
@Data
public class CartBo {
    //要购买的skuid
    private Long skuId;
    //要购买的sku数量
    private Integer num;
    //sku其他属性，从数据库查询填充，不直接使用前台传递的
    private Sku sku;
}
