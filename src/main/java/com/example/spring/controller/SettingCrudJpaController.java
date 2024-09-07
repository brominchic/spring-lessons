package com.example.spring.controller;

import com.example.spring.component.jpa.SettingCrudJpaComponent;
import com.example.spring.model.dto.SettingDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

@RestController
@RequestMapping("/jpa/settings")
@RequiredArgsConstructor
public class SettingCrudJpaController implements CrudJpaController {
    @Autowired
    SettingCrudJpaComponent jpaComponent;

    @GetMapping("/all")
    public List<SettingDto> getAll() {
        return jpaComponent.getAll();
    }

    @PostMapping("/create")
    public SettingDto create(HttpServletRequest request) {
        try {
            Scanner scanner = new Scanner(request.getInputStream(), StandardCharsets.UTF_8);
            String jsonData = scanner.useDelimiter("\\A").next();
            scanner.close();
            return jpaComponent.create(jsonData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/create/batch")
    public List<SettingDto> createBatch(HttpServletRequest request) {
        try {
            Scanner scanner = new Scanner(request.getInputStream(), StandardCharsets.UTF_8);
            String jsonData = scanner.useDelimiter("\\A").next();
            scanner.close();
            return jpaComponent.createBatch(jsonData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
