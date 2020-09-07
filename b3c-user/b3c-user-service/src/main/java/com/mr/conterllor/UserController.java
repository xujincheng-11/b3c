package com.mr.conterllor;

import com.mr.pojo.User;
import com.mr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName UserController
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/25
 * @Version V1.0
 **/
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 先通过name和phone查询用户是否存在
     * */
    @GetMapping(value="check/{data}/{type}")
    public ResponseEntity<Boolean> queryCheck(
            @PathVariable(value="data") String data,
            @PathVariable(value="type") Integer type
    ){
        //400：参数有误
        if(data ==null || type ==null || type > 2){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Boolean result = null;
        try {
            result = userService.queryNameOrPhone(data,type);
            //200：校验成功
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            //500：服务器内部异常
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 用户注册
     * */
    @PostMapping(value="register")
    public ResponseEntity<Void> register(
            @RequestParam(value="username") String username,
            @RequestParam(value="password") String password,
            @RequestParam(value="phone") String phone
    ){
        return userService.register(username,password,phone);
    }

    /**
     * 根据参数中的用户名和密码查询指定用户,用于登录验证
     * */
    @GetMapping("query")
    public ResponseEntity<User> queryNameOrPassword(
            @RequestParam(value = "username",required = true) String username,
            @RequestParam(value = "password",required = true) String password
    ){
        return userService.queryNameOrPassword(username,password);
    }
}
