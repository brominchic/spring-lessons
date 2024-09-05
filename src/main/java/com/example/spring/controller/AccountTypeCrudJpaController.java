package com.example.spring.controller;

import com.example.spring.component.jpa.AccountTypeCrudJpaComponent;
import com.example.spring.model.dto.AccountTypeDto;
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
public class AccountTypeCrudJpaController implements CrudJpaController {
    @Autowired
    AccountTypeCrudJpaComponent jpaComponent;

    @GetMapping("/account-types/all")
    public List<AccountTypeDto> getAll() {
        return jpaComponent.getAll();
    }

    @PostMapping("/account-types/create")
    public void create(HttpServletRequest request) {
        try {
            jpaComponent.create(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/account-types/create/batch")
    public void createBatch(HttpServletRequest request) {
        try {
            jpaComponent.createBatch(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
