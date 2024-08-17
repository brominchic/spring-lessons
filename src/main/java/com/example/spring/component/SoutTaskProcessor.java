package com.example.spring.component;

import com.example.spring.model.TaskInput;
import com.example.spring.model.TaskOutput;
import org.springframework.stereotype.Component;

@Component
public class SoutTaskProcessor implements TaskProcessor {
    @Override
    public TaskOutput process(TaskInput input) {
        System.out.println(input.getInput());
        return TaskOutput.builder()
                .output(input.getInput())
                .taskType("sout")
                .taskId(input.getTaskId())
                .build();
    }

}
