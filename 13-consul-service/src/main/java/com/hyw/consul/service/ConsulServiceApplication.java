package com.hyw.consul.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class ConsulServiceApplication {

    @RequestMapping("/hi")
    public String home() {
        return "hi ,i'm miya";
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(ConsulServiceApplication.class).web(true).run(args);
    }
}