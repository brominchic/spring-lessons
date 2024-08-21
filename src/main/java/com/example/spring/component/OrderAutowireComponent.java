package com.example.spring.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OrderAutowireComponent {

    @Qualifier("logTaskProcessor")
    @Autowired
    private TaskProcessor service;

}
