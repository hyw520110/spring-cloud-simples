package com.hyw.ribbon.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hyw.ribbon.service.service.HelloService;

@RestController
public class HelloControler {   
    @Autowired
    HelloService helloService;
    @RequestMapping(value = "/say")
    public String say(@RequestParam String name){
        return helloService.say(name);
    }   
}
