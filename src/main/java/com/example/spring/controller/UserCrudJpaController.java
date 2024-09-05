package com.example.spring.controller;

import com.example.spring.component.jpa.UserCrudJpaComponent;
import com.example.spring.model.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/jpa")
@RequiredArgsConstructor
public class UserCrudJpaController implements CrudJpaController {
    @Autowired
    UserCrudJpaComponent jpaComponent;

    @GetMapping("/users/all")
    public List<UserDto> getAll() {
        return jpaComponent.getAll();
    }

    @PostMapping("/users/create")
    public void create(HttpServletRequest request) {
        try {
            jpaComponent.create(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/users/create/batch")
    public void createBatch(HttpServletRequest request) {
        try {
            jpaComponent.createBatch(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
