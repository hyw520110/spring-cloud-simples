package com.hyw.config.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ConfigClientApplication {  
    public static void main(String[] args) {        
        SpringApplication.run(ConfigClientApplication.class, args);     
    }   
    
    @Value("${logging.level.org.springframework.web}")
    String loglevel;
    @RequestMapping(value = "/config")
    public String hi(){
        return loglevel;
    }
    
}