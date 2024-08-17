package com.example.spring.component;

import com.example.spring.model.TaskInput;
import com.example.spring.model.TaskOutput;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Map;

@Primary
@Service
public class ProcessorService implements TaskProcessor {
    private final Map<String, TaskProcessor> processors;

    public ProcessorService(Map<String, TaskProcessor> processors) {
        this.processors = processors;
    }

    public TaskOutput process(TaskInput input) {
        return null;
    }
}
