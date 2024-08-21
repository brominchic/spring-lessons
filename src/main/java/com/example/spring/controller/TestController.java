package com.example.spring.controller;

import com.example.spring.component.NumGetterPrototype;
import com.example.spring.component.NumGetterRequest;
import com.example.spring.component.TaskProcessorService;
import com.example.spring.component.TestComponent;
import com.example.spring.config.ApplicationConfig;
import com.example.spring.model.TaskInput;
import com.example.spring.model.TaskOutput;
import com.example.spring.model.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/main")
@RestController
public class TestController {

    private final TestComponent testComponent;
    private final TaskProcessorService taskProcessorService;
    private final NumGetterRequest numGetterRequest;

    @PostMapping
    public User getMain(@RequestBody User user, HttpServletRequest request) {
        log.info("request url is {}", request.getRequestURI());
        return user;
    }

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

}
