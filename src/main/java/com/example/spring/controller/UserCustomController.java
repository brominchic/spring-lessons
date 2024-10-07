package com.example.spring.controller;

import com.example.spring.model.dto.UserWithAccountsDto;
import com.example.spring.service.jpa.UserCrudJpaComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jpa/users")
@RequiredArgsConstructor
public class UserCustomController {
    @Autowired
    private UserCrudJpaComponent component;


    @GetMapping("/{id}/full")
    @Transactional //один запрос
    public UserWithAccountsDto getMain(@PathVariable Long id) {
        return component.getByIdWithAccounts(id);
    }

}
