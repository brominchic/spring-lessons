package com.example.spring.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OrderAutowireComponent {

    @Qualifier("logTaskProcessor")
    @Autowired
    private final TaskProcessor service;

    public OrderAutowireComponent(@Qualifier("logTaskProcessor") TaskProcessor service) {
        this.service = service;
    }
}
