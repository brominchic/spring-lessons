package com.example.spring.component;

import com.example.spring.model.TaskInput;
import com.example.spring.model.TaskOutput;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class LogTaskProcessor implements TaskProcessor {
    @Override
    public TaskOutput process(TaskInput input) {
        log.info(input.getInput());
        return TaskOutput.builder().output(input.getInput()).taskType("log").taskId(input.getTaskId()).build();
    }

}
