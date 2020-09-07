package com.mr.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter//get方法
@NoArgsConstructor//无参构造
@AllArgsConstructor//有参构造
public enum ExceptionEnums {
    //相当于 fianl  PRICE_CANNOT_BE_NULL= new obj(400,"价格不能为空")
    PRICE_CANNOT_BE_NULL(400,"价格不能为空"),
    CATEGORY_CANNOT_BE_NULL(404,"没有分类数据"),
    BRAND_CANNOT_BE_NULL(404,"没有品牌数据"),
    DEL_CATEGORY_ERROR(500,"删除分类报错"),
    SELECT_KEY_CATEGORY_ERROR(404,"查询单条数据报错"),
    SAVE_UPDATE_CATEGORY_ERROR(500,"数据报错"),
    ADD_UPDATE_CATEGORY_ERROR(500,"数据内部错误"),

    ;
    private int code;//状态码

    private String mgs;//提示信息
}
