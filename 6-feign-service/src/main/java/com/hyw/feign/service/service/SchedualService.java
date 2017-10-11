package com.hyw.feign.service.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.hyw.feign.service.service.impl.SchedualServiceImpl;

@FeignClient(value = "simple-service",fallback=SchedualServiceImpl.class)
public interface SchedualService {
    @RequestMapping(value = "/index",method = RequestMethod.GET)
    String say(@RequestParam(value = "name") String name);
}