package com.hyw.feign.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchedualController {
    @Autowired
    SchedualService schedualService;
    @RequestMapping(value = "/say",method = RequestMethod.GET)
    public String sayHi(@RequestParam String name){
        return schedualService.say(name);
    }
}
