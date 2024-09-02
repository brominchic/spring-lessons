package com.example.spring.controller;

import com.example.spring.component.JpaComponent;
import com.example.spring.model.dto.*;
import com.example.spring.model.mapper.Mapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/jpa")
@RequiredArgsConstructor
public class TestJpaController {
    private final JpaComponent jpaComponent;
    private final Map<String, CrudRepository> repositories;
    @Autowired
    private Map<String, Mapper> mappers;
    private Map<String, String> repositoriesNames;
    private Map<String, String> mappersNames;
    private Map<String, Dto> dtoNames;

    @GetMapping("/{entityName}/all")
    public ArrayList<Dto> getAll(@PathVariable String entityName) {

        return jpaComponent.getAll(mappers.get(mappersNames.get(entityName)), repositories.get(repositoriesNames.get(entityName)));
    }

    @PostMapping("/{entityName}/create")
    public void createUser(@PathVariable String entityName, HttpServletRequest request) throws IOException {
        Scanner scanner = new Scanner(request.getInputStream(), StandardCharsets.UTF_8);
        String jsonData = scanner.useDelimiter("\\A").next();
        scanner.close();
        ObjectMapper objectMapper = new ObjectMapper();
        Dto dto = dtoNames.get(entityName);
        dto = objectMapper.readValue(jsonData, dto.getClass());
        jpaComponent.create(mappers.get(mappersNames.get(entityName)), repositories.get(repositoriesNames.get(entityName)), dto);
    }

    @PostMapping("/{entityName}/create/batch")
    public void createUsers(@PathVariable String entityName, HttpServletRequest request) throws IOException {
        Scanner scanner = new Scanner(request.getInputStream(), StandardCharsets.UTF_8);
        String jsonData = scanner.useDelimiter("\\A").next();
        scanner.close();
        ObjectMapper objectMapper = new ObjectMapper();
        Dto dto = dtoNames.get(entityName);
        List<Dto> dtoList = objectMapper.readValue(jsonData, new TypeReference<List<Dto>>() {
        });
        System.out.println(dtoList);
        for (int i = 0; i < dtoList.size(); i++) {
            jpaComponent.create(mappers.get(mappersNames.get(entityName)), repositories.get(repositoriesNames.get(entityName)), dtoList.get(i));
        }

    }

    @PostConstruct
    public void init() {
        this.repositoriesNames = new HashMap<>();
        repositoriesNames.put("users", "userRepository");
        repositoriesNames.put("account-types", "accountTypeRepository");
        repositoriesNames.put("accounts", "accountRepository");
        repositoriesNames.put("settings", "settingRepository");
        repositoriesNames.put("operations", "operationRepository");
        this.mappersNames = new HashMap<>();
        mappersNames.put("users", "userMapper");
        mappersNames.put("account-types", "accountTypeMapper");
        mappersNames.put("accounts", "accountMapper");
        mappersNames.put("settings", "settingMapper");
        mappersNames.put("operations", "operationMapper");
        this.dtoNames = new HashMap<>();
        dtoNames.put("users", new UserDto());
        dtoNames.put("account-types", new AccountTypeDto());
        dtoNames.put("accounts", new AccountDto());
        dtoNames.put("settings", new SettingDto());
        dtoNames.put("operations", new OperationDto());
    }

}
