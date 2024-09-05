package com.example.spring.controller;

import com.example.spring.component.jpa.AccountCrudJpaComponent;
import com.example.spring.model.dto.AccountDto;
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
public class AccountCrudJpaController implements CrudJpaController {
    @Autowired
    AccountCrudJpaComponent jpaComponent;

    @GetMapping("/accounts/all")
    public List<AccountDto> getAll() {
        return jpaComponent.getAll();
    }

    @PostMapping("/accounts/create")
    public void create(HttpServletRequest request) {
        try {
            jpaComponent.create(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/accounts/create/batch")
    public void createBatch(HttpServletRequest request) {
        try {
            jpaComponent.createBatch(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
