package com.hyw.simple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient  
@RestController
public class ZkClientApplication {
    @Autowired  
    private LoadBalancerClient loadBalancer;  
    @Autowired  
    private DiscoveryClient discovery; 
    
    public static void main(String[] args) {
        SpringApplication.run(ZkClientApplication.class, args);
    }
      
    @RequestMapping("/discovery")  
    public Object discovery() {  
        return loadBalancer.choose("zookeeper-server");  
    }  
      
    @RequestMapping("/all")  
    public Object all() {  
        return discovery.getServices();  
    }  
}