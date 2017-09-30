package com.hyw.feign.service;

import org.springframework.stereotype.Component;

@Component
public class SchedualServiceImpl implements SchedualService {
    @Override
    public String say(String name) {
        return "sorry "+name;
    }
}
