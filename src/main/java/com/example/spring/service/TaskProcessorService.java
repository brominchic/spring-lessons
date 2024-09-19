package com.example.spring.service;

import com.example.spring.model.TaskInput;
import com.example.spring.model.TaskOutput;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Builder
@Service
public class TaskProcessorService implements TaskProcessor {

    private final Map<String, TaskProcessor> processors;

    public TaskOutput process(TaskInput input) {
        if (processors.containsKey(input.getTaskType())) {
            return processors.get(input.getTaskType()).process(input);
        } else return null;
    }

}
