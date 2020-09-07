package com.mr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @ClassName ApiGatewayApplication
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/14
 * @Version V1.0
 **/
@SpringBootApplication//表明spring boot启动类
@EnableDiscoveryClient//eureka客户端
@EnableZuulProxy//网关启用
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);}
}
