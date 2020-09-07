package com.mr.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @ClassName RegistryApplication
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/14
 * @Version V1.0
 **/
@SpringBootApplication//启动类
@EnableEurekaServer//这是eureka注册服务
public class RegistryApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegistryApplication.class);
    }

}
