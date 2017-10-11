package com.hyw.redis.service;

import javax.annotation.PostConstruct;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * 获取RedissonClient连接类
 */
@Component
public class RedissonConnector {
    RedissonClient redisson;
    @PostConstruct
    public void init(){
        redisson = Redisson.create();
    }

    public RedissonClient getClient(){
        return redisson;
    }

}