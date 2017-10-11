package com.hyw.lucy.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@SpringBootApplication
@EnableEurekaClient
@RestController
@EnableHystrix
@EnableHystrixDashboard
public class LucyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LucyServiceApplication.class, args);
    }

    @Value("${server.port}")
    String port;
    @RequestMapping("/hi")
   //@HystrixCommand(fallbackMethod = "hiError")
    public String home(@RequestParam String name) {
        return "hi "+name+",i  am lucy and from port:" +port;
    }

    private String hiError(@RequestParam String name) {
        return "hi,"+name+",sorry,error!";
    }
}