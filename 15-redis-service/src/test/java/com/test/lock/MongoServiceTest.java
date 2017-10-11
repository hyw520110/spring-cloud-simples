package com.test.lock;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hyw.redis.service.Application;
import com.hyw.redis.service.AquiredLockWorker;
import com.hyw.redis.service.RedisLocker;
@RunWith(SpringRunner.class)
@SpringBootTest(classes =Application.class )
public class MongoServiceTest {
    
    private static final Logger logger = LoggerFactory.getLogger(MongoServiceTest.class);

    
}
