package com.mr.service;

import com.mr.mapper.UserMapper;
import com.mr.pojo.User;
import com.mr.util.Md5Utils;
import com.mr.util.PasswordUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @ClassName UserService
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/25
 * @Version V1.0
 **/
@Service
public class UserService {
    @Resource
    private UserMapper userMapper;
    /*
    * 先通过name和phone查询用户是否存在
    * */
    public Boolean queryNameOrPhone(String data, Integer type) {
        User user = new User();
        //要校验的数据类型：1，用户名；2，手机；
        if(type == 1){
            user.setUsername(data);
        }else if(type == 2){
            user.setPhone(data);
        }
        //selectCount(user):返回的是查询的数据条数, 0 不存在,可以注册; 其他 存在,不能注册;
        return userMapper.selectCount(user) == 0;
    }

    /*
    * 用户注册
    * */
    public ResponseEntity register(String username, String password, String phone) {
        //400：参数有误，注册失败
        if(username == null || password == null || phone == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            User user = new User();
            user.setUsername(username);
            user.setPhone(phone);
            //设置注册时间
            user.setCreated(new Date());

            // md5加密;设置uuid为盐
            user.setSalt(Md5Utils.generateSalt());
            //先加密加盐,加密过程中使用随机码作为salt加盐,在加密
            user.setPassword(Md5Utils.md5Hex(Md5Utils.md5Hex(password),user.getSalt()));
            System.out.println(Md5Utils.md5Hex(Md5Utils.md5Hex(password),user.getSalt()));

            //64位算法加密,不可破解
           /* user.setSalt("");
            user.setPassword(PasswordUtil.encode(password));
            System.out.println("----" + PasswordUtil.encode(password,user.getSalt().getBytes()));*/

            userMapper.insertSelective(user);
            //201：注册成功
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            //500：服务器内部异常，注册失败
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    根据参数中的用户名和密码查询指定用户,用于登录验证
    * */
    public ResponseEntity<User> queryNameOrPassword(String username, String password) {
        User ex = new User();
        ex.setUsername(username);
        //根据用户名查询用户，校验用户名，校验密码是否正确
        User user = userMapper.selectOne(ex);
        if(user == null){
            //400：用户名或密码错误
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        //第一种方法 通过Md5加密 ; 登录新的密码与Md5注册加密的密码比较
       if(user.getPassword().equals(Md5Utils.md5Hex(Md5Utils.md5Hex(password),user.getSalt()))){
            //200：查询成功
            return ResponseEntity.ok(user);
        }else{
            //500：服务器内部异常，查询失败
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


        // 第二种方法 64位算法加密,登录新的密码与64位算法加密的密码比较
       /* if(PasswordUtil.comparePassword(password,user.getPassword())){
            //200：查询成功
            return ResponseEntity.ok(user);
        }else{
            //500：服务器内部异常，查询失败
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }*/
    }
}
