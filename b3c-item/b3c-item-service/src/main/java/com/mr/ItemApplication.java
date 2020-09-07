package com.mr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient//开启eureka客户端
//配置啦druid连接池,就不需要该注解
//@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)//报错先加上
//mapper中定义@Mapper注解,就不用写此注解
//@MapperScan("com.mr.mapper")
public class ItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemApplication.class, args);
    }
}
