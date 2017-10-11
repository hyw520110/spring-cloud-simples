package com.hyw.mongo.service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hyw.mongo.service.model.Customer;

public interface CustomerRepository extends MongoRepository<Customer, String> {

    public Customer findByFirstName(String firstName);
    public List<Customer> findByLastName(String lastName);

}