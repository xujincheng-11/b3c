package com.mr.controller;

import com.mr.bo.UserInfo;
import com.mr.common.utils.CookieUtils;
import com.mr.config.JwtConfig;
import com.mr.service.AuthService;
import com.mr.util.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName AuthController
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/26
 * @Version V1.0
 **/
@RestController
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtConfig jwtConfig;
    /*
    *   登录
    * */
    @PostMapping(value="login")
    public ResponseEntity<Void> login(
            @RequestParam(value="username") String username,
            @RequestParam(value="password") String password,
            HttpServletRequest request,
            HttpServletResponse response
    ){
        //校验账号密码是否正确
        String token = authService.auth(username,password);
        //如果token账号密码为空或错误，返回401无权限
        if(StringUtils.isEmpty(token)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        //调用common项目模块中的工具类,将生成token值 就放入cookie中
        CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),token,jwtConfig.getCookieMaxAge(),true);
        return ResponseEntity.ok(null);
    }

    /**
     * 1.用于用户登录后,展示用户的登录状态
     * 2.用于用户刷新不同的页面,就等于刷新token,重新生成一份token,重新设置啦过期时间
     * */
    @GetMapping(value="verify") //verify:核实,查验用户登录状态
    public ResponseEntity<UserInfo> verifyUser(
            @CookieValue(value="B2C_TOKEN") String token,
            HttpServletRequest request,
            HttpServletResponse response
    ){
        System.out.println("取得token:"+token);

        try {
            //根据获取的token通过公钥解密(id,username),展示用户的登录状态(就是展示那个用户登录的)
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            // 证明用户是正确登陆状态，用于用户刷新不同的页面,就等于刷新token,重新生成一份token,重新设置啦过期时间,这样登陆状态就又刷新30分钟过期时间了
            //通过公钥解密userInfo获取id,username后,在通过私钥加密,jwt的过期时间(分钟),重新生成token,
            String newToken = JwtUtils.generateToken(userInfo,jwtConfig.getPrivateKey(), jwtConfig.getExpire());

            //调用common项目模块中的工具类,将新生成newToken值 就放入cookie中;(将新token写入cookie 过期时间延长了,用于用户刷新不同的页面)
            CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),newToken,jwtConfig.getCookieMaxAge(),true);

            //返回通过公钥解密的用户信息,展示用户的登录状态
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
            //访问权限受限,授权时间过期, 证明token无效，直接返回403
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
