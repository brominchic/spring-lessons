package com.example.spring;

import com.example.spring.config.ApplicationConfig;
import com.example.spring.model.TaskInput;
import com.example.spring.model.TaskOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@SpringBootApplication
@Slf4j
public class ExampleApplication {
    public static void main(String[] args) throws URISyntaxException {
        SpringApplication.run(ExampleApplication.class, args);
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        RestTemplate restTemplate = (RestTemplate) applicationContext.getBean("restTemplate");
        TaskOutput taskOutput = restTemplate.postForObject(new URI("http://localhost:8080/example-application/main/process"), new TaskInput("logTaskProcessor", "1", "nebrom"), TaskOutput.class);
        log.info(taskOutput.getOutput());
    }
}
