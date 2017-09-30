package com.hyw.ribbon.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class HelloService { 
    @Autowired
    RestTemplate restTemplate;
    
    @HystrixCommand(fallbackMethod="fallback")
    public String say(String name) {
        return restTemplate.getForObject("http://simple-service/index?name="+name,String.class);
    }   
    private String fallback(String name) {
        return "hi,"+name+",sorry,error!";
    }
}