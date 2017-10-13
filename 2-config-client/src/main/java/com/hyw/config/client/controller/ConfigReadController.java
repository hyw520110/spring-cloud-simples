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
    
    /**
     * 从配置中心获取一个配置项
     * @author:  heyiwu 
     * @return
     */
    @RequestMapping(value = "/loglevel")
    public String loglevel(){
        return loglevel;
    }
    /**
     * 从配置中心获取一个加密配置项(配置项加密存储，配置中心负责解密)
     * @author:  heyiwu 
     * @return
     */
    @RequestMapping(value = "/pwd")
    public String pwd(){
        return pwd;
    }
}
