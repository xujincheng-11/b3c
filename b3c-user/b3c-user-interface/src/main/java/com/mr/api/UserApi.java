package com.mr.api;

import com.mr.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserApi {
    /**
     * 先通过name和phone查询用户是否存在
     * */
    @GetMapping(value="check/{data}/{type}")
    public Boolean queryCheck(
            @PathVariable(value="data") String data,
            @PathVariable(value="type") Integer type
    );

    /**
     * 用户注册
     * */
    @PostMapping(value="register")
    public void register(
            @RequestParam(value="username") String username,
            @RequestParam(value="password") String password,
            @RequestParam(value="phone") String phone
    );

    /**
     * 根据参数中的用户名和密码查询指定用户,用于登录验证
     * */
    @GetMapping("query")
    public User queryNameOrPassword(
            @RequestParam(value = "username",required = true) String username,
            @RequestParam(value = "password",required = true) String password
    );
}
