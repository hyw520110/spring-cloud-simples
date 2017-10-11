package com.hyw.feign.service.service.impl;

import org.springframework.stereotype.Component;

import com.hyw.feign.service.service.SchedualService;

@Component
public class SchedualServiceImpl implements SchedualService {
    @Override
    public String say(String name) {
        return "sorry "+name;
    }
}
