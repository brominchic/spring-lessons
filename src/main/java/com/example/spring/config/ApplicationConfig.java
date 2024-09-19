package com.example.spring.config;

import com.example.spring.model.User;
import com.example.spring.service.LogTaskProcessor;
import com.example.spring.service.NumGetterPrototype;
import com.example.spring.service.SoutTaskProcessor;
import com.example.spring.service.TaskProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAspectJAutoProxy
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

    @Bean(value = "restTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
