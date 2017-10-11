package com.hyw.zipkin.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.sleuth.zipkin.stream.EnableZipkinStreamServer;
@SpringBootApplication
@EnableEurekaClient
//@EnableZipkinServer
@EnableZipkinStreamServer
public class ZipkinServerApplication {   
    public static void main(String[] args) {
        SpringApplication.run(ZipkinServerApplication.class, args);
    }
}