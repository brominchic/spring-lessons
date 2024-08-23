package com.example.spring.aspects;

import com.example.spring.component.TaskProcessor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("target(target)")
    public void logTaskProcessor(TaskProcessor target) {
    }

    @Before("logTaskProcessor(target)")
    public void beforeLogTaskProcessor(TaskProcessor target) {
        log.info("Вызван {}", target.getClass().getName());
    }

    @After("logTaskProcessor(target)")
    public void afterLogTaskProcessor(TaskProcessor target) {
        log.info("отработал {}", target.getClass().getName());
    }


}
