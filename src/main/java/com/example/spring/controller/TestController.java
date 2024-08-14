package com.example.spring.controller;

import com.example.spring.component.TestComponent;
import com.example.spring.users.User;
import org.springframework.web.bind.annotation.*;


@RestController
public class TestController {

    private final TestComponent testComponent;

    public TestController(TestComponent testComponent) {
        this.testComponent = testComponent;
    }

    @PostMapping("/main")
    public String getMain(@RequestBody User user) {
        return testComponent.getPage(user.getFirstname(), user.getLastname());
    }

    @GetMapping("/test")
    public String test() {
        return testComponent.test();
    }

    @GetMapping("/main/{firstname}/{lastname}")
    public String getMainByPath(@PathVariable String firstname, @PathVariable String lastname) {
        return testComponent.getPage(firstname, lastname);
    }

    @GetMapping("/main")
    public String getMainByRequestParam(@RequestParam String firstname, @RequestParam String lastname) {
        return testComponent.getPage(firstname, lastname);
    }
}
