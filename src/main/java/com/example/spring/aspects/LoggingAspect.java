package com.example.spring.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(com.example.spring.component.TaskProcessor+)")
    public void logTaskProcessor() {
    }

    @Before("logTaskProcessor()")
    public void beforeLogTaskProcessor(JoinPoint joinPoint) {
        log.info("Requested {}", joinPoint.getArgs());
        log.info("Requested {}", joinPoint.getKind());
        log.info("Requested {}", joinPoint.getSignature());
    }

    @After("logTaskProcessor()")
    public void afterLogTaskProcessor(JoinPoint joinPoint) {
        log.info("Finished {}", joinPoint.getTarget().getClass().getName());
    }


}
