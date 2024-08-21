package com.example.spring.config;

import com.example.spring.component.LogTaskProcessor;
import com.example.spring.component.NumGetterPrototype;
import com.example.spring.component.SoutTaskProcessor;
import com.example.spring.component.TaskProcessor;
import com.example.spring.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ApplicationConfig {

    @Bean
    public User userBean() {
        return User.builder().build();
    }

    @Bean(value = "logTaskProcessor")
    public TaskProcessor logTaskProcessorBean() {
        return LogTaskProcessor.builder().build();
    }

    @Bean(value = "soutTaskProcessor")
    public TaskProcessor soutTaskProcessorBean() {
        return SoutTaskProcessor.builder().build();
    }

    @Bean(value = "numGetterPrototype")
    @Scope("prototype")
    public NumGetterPrototype numGetterPrototype() {
        return new NumGetterPrototype();
    }

}
