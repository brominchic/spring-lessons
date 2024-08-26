package com.example.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URISyntaxException;

@SpringBootApplication
@Slf4j
public class ExampleApplication {
    public static void main(String[] args) throws URISyntaxException {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
