package com.example.spring.component;

import com.example.spring.model.TaskInput;
import com.example.spring.model.TaskOutput;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProcessorService {
    private final Map<String, TaskProcessor> processors;

    public ProcessorService(Map<String, TaskProcessor> processors) {
        this.processors = processors;
    }

    public TaskOutput process(TaskInput input) {
        return null;
    }
}
