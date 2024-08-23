package com.example.spring;

import com.example.spring.model.TaskInput;
import com.example.spring.model.TaskOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public class RestTemplateExample {
    public static void main(String[] args) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        TaskOutput taskOutput = restTemplate.postForObject(new URI("http://localhost:8080/example-application/main/process"), new TaskInput("logTaskProcessor", "1", "nebrom"), TaskOutput.class);
        log.info(taskOutput.getOutput());
    }
}
