package com.example.spring.controller;

import com.example.spring.config.ApplicationConfig;
import com.example.spring.model.TaskInput;
import com.example.spring.model.TaskOutput;
import com.example.spring.service.NumGetterPrototype;
import com.example.spring.service.TaskProcessorService;
import com.example.spring.service.component.NumGetterRequest;
import com.example.spring.service.component.RestCallerComponent;
import com.example.spring.service.component.TestComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/main")
@RestController
public class TestController {

    private final TestComponent testComponent;
    private final TaskProcessorService taskProcessorService;
    private final NumGetterRequest numGetterRequest;
    private final RestCallerComponent restCallerComponent;

    @PostMapping("/process")
    public TaskOutput doProcess(@RequestBody TaskInput taskInput) {
        return taskProcessorService.process(taskInput);
    }

    @GetMapping("/{name}/{lastname}")
    public String getMainByPath(@PathVariable(name = "name") String firstname, @PathVariable String lastname) {
        return testComponent.getPage(firstname, lastname);
    }

    @GetMapping
    public String getMainByRequestParam(@RequestParam String firstname, @RequestParam String lastname) {
        return testComponent.getPage(firstname, lastname);
    }

    @GetMapping("/num/prototype")
    public int getNumPrototype() {
        log.info("Новый запрос");
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        NumGetterPrototype numGetterPrototype = (NumGetterPrototype) applicationContext.getBean("numGetterPrototype");
        log.info(String.valueOf(numGetterPrototype.getNum()));
        numGetterPrototype = (NumGetterPrototype) applicationContext.getBean("numGetterPrototype");
        log.info(String.valueOf(numGetterPrototype.getNum()));
        log.info("конец обработки запроса");
        return numGetterPrototype.getNum();
    }

    @GetMapping("/num/request")
    public int getNumRequest() {
        log.info("Новый запрос");
        log.info(String.valueOf(numGetterRequest.getNum()));
        log.info(String.valueOf(numGetterRequest.getNum()));
        log.info(String.valueOf(numGetterRequest.getNum()));
        log.info(String.valueOf(numGetterRequest.getNum()));
        log.info("конец обработки запроса");
        return numGetterRequest.getNum();
    }

    @GetMapping("/init")
    public void getInit() throws URISyntaxException {
        restCallerComponent.init();
    }
}

