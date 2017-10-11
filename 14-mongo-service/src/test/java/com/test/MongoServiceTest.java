package com.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hyw.mongo.service.Application;
import com.hyw.mongo.service.model.Customer;
import com.hyw.mongo.service.repository.CustomerRepository;
@RunWith(SpringRunner.class)
@SpringBootTest(classes =Application.class )
public class MongoServiceTest {
    
    private static final Logger logger = LoggerFactory.getLogger(MongoServiceTest.class);

    @Autowired
    CustomerRepository customerRepository;

    
    @Test
    public void mongodbIdTest(){
    Customer customer=new Customer("lxdxil","dd");
            customer=customerRepository.save(customer);
            logger.info( "mongodbId:"+customer.getId());
    }
}
