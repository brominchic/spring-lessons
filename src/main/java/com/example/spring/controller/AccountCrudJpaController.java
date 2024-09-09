package com.example.spring.controller;

import com.example.spring.model.dto.AccountDto;
import com.example.spring.service.jpa.AccountCrudJpaComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/jpa/accounts")
@RequiredArgsConstructor
public class AccountCrudJpaController implements CrudJpaController<AccountDto> {
    @Autowired
    AccountCrudJpaComponent jpaComponent;

    @GetMapping("/all")
    public List<AccountDto> getAll() {
        return jpaComponent.getAll();
    }

    @PostMapping("/create")
    public AccountDto create(@RequestBody AccountDto input) throws IOException {
        return jpaComponent.create(input);
    }

    @PostMapping("/create/batch")
    public List<AccountDto> createBatch(@RequestBody List<AccountDto> input) {
        return jpaComponent.createBatch(input);
    }

}
