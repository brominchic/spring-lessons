package com.example.spring.controller;

import com.example.spring.component.TestComponent;
import com.example.spring.users.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/main")
@RestController
public class TestController {

    private final TestComponent testComponent;

    @PostMapping
    public User getMain(@RequestBody User user, HttpServletRequest request) {
        log.info("request url is {}", request.getRequestURI());
        return user;
    }


    @GetMapping("/{name}/{lastname}")
    public String getMainByPath(@PathVariable(name = "name") String firstname, @PathVariable String lastname) {
        return testComponent.getPage(firstname, lastname);
    }

    @GetMapping
    public String getMainByRequestParam(@RequestParam String firstname, @RequestParam String lastname) {
        return testComponent.getPage(firstname, lastname);
    }

}
