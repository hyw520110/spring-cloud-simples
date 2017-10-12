package com.hyw.config.client.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping(value = "/config")
public class ConfigReadController {
    @Value("${logging.level.org.springframework.web}")
    private String loglevel;
    @Value("${mysqldb.datasource.password}")
    private String pwd;
    
    @RequestMapping(value = "/loglevel")
    public String loglevel(){
        return loglevel;
    }

    @RequestMapping(value = "/pwd")
    public String pwd(){
        return pwd;
    }
}
