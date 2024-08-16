package com.example.spring.controller;

import com.example.spring.component.TestComponent;
import com.example.spring.users.User;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/main")
@RestController
public class TestController {

    private final TestComponent testComponent;

    public TestController(TestComponent testComponent) {
        this.testComponent = testComponent;
    }

    @PostMapping()
    public User getMain(@RequestBody User user) {
        return user;
    }


    @GetMapping("/{firstname}/{lastname}")
    public String getMainByPath(@PathVariable String firstname, @PathVariable String lastname) {
        return testComponent.getPage(firstname, lastname);
    }

    @GetMapping()
    public String getMainByRequestParam(@RequestParam String firstname, @RequestParam String lastname) {
        return testComponent.getPage(firstname, lastname);
    }

}
