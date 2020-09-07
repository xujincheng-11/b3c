package com.mr.common.advice;

import com.mr.common.enums.ExceptionEnums;
import com.mr.common.exception.MrException;
import com.mr.common.vo.ExceptionRusult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @ClassName CommonExceptionHandler
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/14
 * @Version V1.0
 **/
@ControllerAdvice//标明为控制层通知类
public class CommonExceptionHandler {
    //拦截指定异常,级别越高,范围越广
    @ExceptionHandler(MrException.class)
    public ResponseEntity<ExceptionRusult> exceptionHandle(MrException re){
        //接收自定义异常中的枚举信息
        ExceptionEnums ex = re.getExceptionEnums();

        System.out.println("异常被扑捉到:"+ex.getMgs());

        //规定返回的状态码400，然后异常的提示信息设置到body中
        /**
         * 封装返回结果*
         * */
        return ResponseEntity.status(ex.getCode()).body(new ExceptionRusult(ex));
    }
}
