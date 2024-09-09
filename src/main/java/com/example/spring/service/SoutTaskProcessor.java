package com.example.spring.service;

import com.example.spring.model.TaskInput;
import com.example.spring.model.TaskOutput;
import lombok.Builder;

@Builder
public class SoutTaskProcessor implements TaskProcessor {
    @Override
    public TaskOutput process(TaskInput input) {
        System.out.println(input.getInput());
        return TaskOutput.builder().output(input.getInput()).taskType("sout").taskId(input.getTaskId()).build();
    }

}
