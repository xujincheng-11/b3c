package com.mr.test;

import com.mr.util.JwtUtils;
import com.mr.util.RsaUtils;
import com.mr.bo.UserInfo;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTokenTest {

    //公钥位置
    private static final String pubKeyPath = "E:/1907/exercise/project/b2c001/reskey/rea.pub";
    //私钥位置
    private static final String priKeyPath = "E:/1907/exercise/project/b2c001/reskey/rea.pri";
    //公钥对象
    private PublicKey publicKey;
    //私钥对象
    private PrivateKey privateKey;


    /**
     * 生成公钥私钥 根据密文
     * 执行生成公钥私钥时,先注释获取密钥的方法@Before
     * @throws Exception
     */
    @Test
    public void genRsaKey() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "9527");
    }


    /**
     * 从文件中读取公钥私钥
     * @throws Exception
     */
    @Before
    public void getKeyByRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    /**
     * 根据用户信息结合私钥生成token
     * @throws Exception
     */
    @Test
    public void genToken() throws Exception {
        // 根据实体类,私钥生成token, token的失效时间(分钟)
        //用户对象(实体类)
        UserInfo userInfo = new UserInfo(1l, "tomcat");
        String token = JwtUtils.generateToken(userInfo, privateKey, 2);
        System.out.println("user-token = " + token);
    }


    /**
     * 结合公钥解析token
     * @throws Exception
     */
    @Test
    public void parseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJ0b21jYXQiLCJleHAiOjE1OTg0NDk0OTd9.CeVFejeAOQD7IWn_4fmVwDypHwpGSWOQVXYGR8Nk_XKN5UGgAYKW7FNtSAzYSIH0FhykpsTrR0ILfgs-vI3hqgrcJ1TDNS5bKKqOHOSev9hI91WtOzJtNRgop2xLYylkvj7l6VyhviiyyJVNNLMhjwglwc5TuUdWaMqqB3uj7xw";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }

}


