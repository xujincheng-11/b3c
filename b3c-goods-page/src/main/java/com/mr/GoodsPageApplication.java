package com.mr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName GoodsPageApplication
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/17
 * @Version V1.0
 **/
@SpringBootApplication//启动类
@EnableDiscoveryClient//开启eureka客户端
//@EnableEurekaClient //开启eureka客户端
@EnableFeignClients//开启feign接口调用
public class GoodsPageApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodsPageApplication.class);
    }
}
