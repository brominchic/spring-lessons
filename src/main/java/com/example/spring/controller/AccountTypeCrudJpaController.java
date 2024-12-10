package com.example.spring.controller;

import com.example.spring.model.dto.AccountTypeDto;
import com.example.spring.service.jpa.AccountTypeCrudJpaComponent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/jpa/account-types")
@RequiredArgsConstructor
public class AccountTypeCrudJpaController implements CrudJpaController<AccountTypeDto> {
    @Autowired
    AccountTypeCrudJpaComponent jpaComponent;

    @GetMapping("/all")
    public List<AccountTypeDto> getAll() {
        return jpaComponent.getAll();
    }

    @PostMapping("/create")
    public AccountTypeDto create(@RequestBody @Valid AccountTypeDto input) throws IOException {
        return jpaComponent.create(input);
    }

    @PostMapping("/create/batch")
    public List<AccountTypeDto> createBatch(@RequestBody List<AccountTypeDto> input) {
        return jpaComponent.createBatch(input);
    }


}
