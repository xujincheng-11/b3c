package com.mr.service;

import com.mr.bo.UserInfo;
import com.mr.client.UserClient;
import com.mr.config.JwtConfig;
import com.mr.pojo.User;
import com.mr.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName AuthService
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/26
 * @Version V1.0
 **/
@Service
public class AuthService {
    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 校验账号密码，如果正确，组装token返回
     * */
    public String auth(String username, String password) {
        //调用用户中心服务api，校验账号密码
        User user = userClient.queryNameOrPassword(username,password);
        //判断user==null查询失败，账号密码错误，返回null
        if(user == null){
            return null;
        }

        // 根据查询的user获取id,username,私钥,jwt的过期时间(分钟),生成token,
        UserInfo userInfo = new UserInfo(user.getId(),user.getUsername());
        try {
            String token = JwtUtils.generateToken(userInfo, jwtConfig.getPrivateKey(), jwtConfig.getExpire());
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
