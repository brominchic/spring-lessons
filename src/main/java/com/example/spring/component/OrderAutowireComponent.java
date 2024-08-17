package com.example.spring.component;

import org.springframework.stereotype.Component;

@Component
public class OrderAutowireComponent {
    private final TaskProcessor service;

    public OrderAutowireComponent(TaskProcessor service) {
        this.service = service;
    }
}
