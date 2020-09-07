package com.mr.common.exception;

import com.mr.common.enums.ExceptionEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName MrException
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/15
 * @Version V1.0
 **/

@Getter//get方法
@AllArgsConstructor//有参构造函数
@NoArgsConstructor//无参构造函数
public class MrException extends RuntimeException{
    //1.继承RuntimeException
    //2.状态码,异常提示信息,时间戳
    //3.定义两个属性 code , mgs
    //4.用枚举来规定接收的参数(因为是常量,所以用枚举)

    //成员变量，枚举类型 通过有参构造实例化
    private ExceptionEnums exceptionEnums;
}
