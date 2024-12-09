package com.example.spring.controller;

import com.example.spring.model.dto.UserDto;
import com.example.spring.service.jpa.UserCrudJpaComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/jpa/users")
@RequiredArgsConstructor
public class UserCrudJpaController implements CrudJpaController<UserDto> {
    @Autowired
    UserCrudJpaComponent jpaComponent;

    @GetMapping("/all")
    public List<UserDto> getAll() {
        return jpaComponent.getAll();
    }

    @PostMapping("/create")
    public UserDto create(@RequestBody @Validated UserDto input) throws IOException {
        return jpaComponent.create(input);
    }

    @PostMapping("/create/batch")
    public List<UserDto> createBatch(@RequestBody List<UserDto> input) {
        return jpaComponent.createBatch(input);
    }

}
