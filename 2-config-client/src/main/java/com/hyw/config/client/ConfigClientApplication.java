package com.hyw.config.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ConfigClientApplication {  
    @Value("${logging.level.org.springframework.web}")
    String loglevel;
    
    public static void main(String[] args) {        
        SpringApplication.run(ConfigClientApplication.class, args);     
    }   
    
    @RequestMapping(value = "/config")
    public String loglevel(){
        return loglevel;
    }
}