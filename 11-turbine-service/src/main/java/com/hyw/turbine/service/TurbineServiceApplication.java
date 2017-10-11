package com.hyw.turbine.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@SpringBootApplication
@EnableTurbine
public class TurbineServiceApplication {
    public static void main(String[] args) {	
            new SpringApplicationBuilder(TurbineServiceApplication.class).web(true).run(args);
    }
}