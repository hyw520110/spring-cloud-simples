package com.hyw.config.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class ConfigClientApplication {  
    @Value("${server.port}")
    private int port;
    public static void main(String[] args) { 
        SpringApplication.run(ConfigClientApplication.class, args);     
    } 
    
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(ConfigClientApplication.class.getPackage().getName()+".controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("应用服务")
                .description("分布式配置中心客户端")
                .termsOfServiceUrl("http://localhost:"+port)
                .contact(new Contact("hyw","https://github.com/hyw520110/","419140278@qq.com"))
                .version("1.0")
                .build();
    }
}