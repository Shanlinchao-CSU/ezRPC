package com.example.provider01;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.provider01.mapper")
public class Provider01Application {
    public static void main(String[] args) {
        SpringApplication.run(Provider01Application.class, args);
    }
}
