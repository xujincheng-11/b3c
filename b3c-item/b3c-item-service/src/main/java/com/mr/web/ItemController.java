package com.mr.web;

import com.mr.common.enums.ExceptionEnums;
import com.mr.common.exception.MrException;
import com.mr.service.ItemService;
import com.mr.service.pojo.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ItemController
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/14
 * @Version V1.0
 **/
@RestController
@RequestMapping("item")
public class ItemController {
    /**
     * 现在要做新增商品功能，需要接受下面的参数
     *
     * ```
     * price:价格
     * name:名称
     * ```
     *
     * 然后校验数据：价格不能为空
     *
     * 新增时，自动生成商品id返回商品。*/

    @Autowired
    private ItemService service;

    @PostMapping
    public ResponseEntity<Item> addItem(Item item) {

         if (item.getPrice() == null) {
            //状态码,提示信息
            //return 一个既包含状态码,又包含提示信息

//            return ResponseEntity .status(HttpStatus.BAD_REQUEST).body("价格参数不能为空");
            //用ResponseEntity返回 1.泛型,2.不符合开发规范
            //抛出异常
            //throw new RuntimeException("价格参数不能为空"+400);

             //抛出自定义异常
            // throw new MrException(ExceptionEnums.PRICE_CANNOT_BE_NULL);
             throw new MrException(ExceptionEnums.PRICE_CANNOT_BE_NULL);
        }
        return ResponseEntity.status(HttpStatus.OK).body(service.saveItem(item));
    }
}
