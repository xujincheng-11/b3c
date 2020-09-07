package com.mr.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2 //开启swagger2
public class SwaggerConfig {
    @Bean
    public Docket docket() {
        //指定访问地址 扫描的controler包，生成文档
        return new Docket(DocumentationType.SWAGGER_2)
                .host("localhost:8089")
                .enable(true)//swagger默认开启,不写也是默认,如果是false,则swagger不能再浏览器访问
                .groupName("订单api")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mr.order.web"))
                .paths(PathSelectors.any())//加载过滤指定包下的所有文件
                .build();
    }
    @Bean
    public Docket docket1() {
        //指定访问地址 扫描的controler包，生成文档
        return new Docket(DocumentationType.SWAGGER_2)
                .host("localhost:8081")
                .enable(true)//swagger默认开启,不写也是默认,如果是false,则swagger不能再浏览器访问
                .groupName("商品服务api")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mr.web"))
                .paths(PathSelectors.any())//加载过滤指定包下的所有文件
                .build();
    }
    private ApiInfo apiInfo() {
        //接口文档的标题，介绍版本号等，
        return new ApiInfoBuilder()
                .title("b2c商城")
                .description("b2c系统接口文档")
                .version("1.0.0")
                .build();
    }
}