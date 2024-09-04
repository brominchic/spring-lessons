package com.example.spring.controller;

import com.example.spring.component.jpa.CrudJpaComponent;
import com.example.spring.model.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@RestController
@RequestMapping("/jpa")
@RequiredArgsConstructor
public class TestJpaController {
    private final Map<String, CrudJpaComponent> jpaComponents;
    private Map<String, Object> dtoNames;
    private Map<String, String> jpaComponentsNames;

    @GetMapping("/{entityName}/all")
    public List<Dto> getAll(@PathVariable String entityName) {
        CrudJpaComponent<Dto> jpaComponent = jpaComponents.get(jpaComponentsNames.get(entityName));
        return jpaComponent.getAll();
    }

    @PostMapping("/{entityName}/create")
    public void create(@PathVariable String entityName, HttpServletRequest request) throws IOException {
        Scanner scanner = new Scanner(request.getInputStream(), StandardCharsets.UTF_8);
        String jsonData = scanner.useDelimiter("\\A").next();
        scanner.close();
        ObjectMapper objectMapper = new ObjectMapper();
        Dto dto = dtoNames.get(entityName);
        dto = objectMapper.readValue(jsonData, dto.getClass());
        CrudJpaComponent<Dto> jpaComponent = jpaComponents.get(jpaComponentsNames.get(entityName));
        jpaComponent.create(dto);
    }

    @PostMapping("/{entityName}/create/batch")
    public void createBatch(@PathVariable String entityName, HttpServletRequest request) throws IOException {
        CrudJpaComponent<Dto> jpaComponent = jpaComponents.get(jpaComponentsNames.get(entityName));
        jpaComponent.createBatch(request);
    }

    @PostConstruct
    public void init() {
        this.jpaComponentsNames = new HashMap<>();
        jpaComponentsNames.put("users", "userCrudJpaComponent");
        jpaComponentsNames.put("account-types", "accountTypeCrudJpaComponent");
        jpaComponentsNames.put("accounts", "accountCrudJpaComponent");
        jpaComponentsNames.put("settings", "settingCrudJpaComponent");
        jpaComponentsNames.put("operations", "operationCrudJpaComponent");
        this.dtoNames = new HashMap<>();
        dtoNames.put("users", new UserDto());
        dtoNames.put("account-types", new AccountTypeDto());
        dtoNames.put("accounts", new AccountDto());
        dtoNames.put("settings", new SettingDto());
        dtoNames.put("operations", new OperationDto());
    }

}
