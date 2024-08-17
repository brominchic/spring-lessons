package com.example.spring.component;

import com.example.spring.model.TaskInput;
import com.example.spring.model.TaskOutput;

public interface TaskProcessor {
    /**
     * обработать задание
     */
    TaskOutput process(TaskInput input);

}
