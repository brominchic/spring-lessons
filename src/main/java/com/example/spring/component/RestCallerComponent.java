package com.example.spring.component;

import com.example.spring.model.TaskInput;
import com.example.spring.model.TaskOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestCallerComponent {

    private final RestTemplate restTemplate;

    public void init() throws URISyntaxException {
        TaskOutput taskOutput = restTemplate.postForObject(new URI("http://localhost:8080/example-application/main/process"), new TaskInput("logTaskProcessor", "1", "nebrom"), TaskOutput.class);
        log.info(taskOutput.getOutput());
    }

}
