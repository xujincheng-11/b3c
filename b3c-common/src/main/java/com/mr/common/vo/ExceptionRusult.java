package com.mr.common.vo;

import com.mr.common.enums.ExceptionEnums;
import lombok.Data;

/**
 * @ClassName ExceptionRusult
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/15
 * @Version V1.0
 **/

@Data
public class ExceptionRusult {
    //自定义的前段返回信息;优化返回格式
    /**
     * {
     *   "code": 500,
     *   "mgs": "名称太长,数据库长度不够",
     *   "timestamp": 1594783260540,
     *   "path": null,
     *   "error": null
     * }*/
    private int code;//状态码

    private String mgs;//提示信息

    private Long timestamp;//时间戳

    private String path;//访问路径

    private String error;//错误的提示信息

    //有参构造
    public ExceptionRusult (ExceptionEnums enums){
        this.code = enums.getCode();
        this.mgs = enums.getMgs();
        this.timestamp = System.currentTimeMillis();//当前系统的时间毫秒值
    }
}
