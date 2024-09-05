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
import java.util.List;

@RestController
@RequestMapping("/jpa")
@RequiredArgsConstructor
public class SettingCrudJpaController implements CrudJpaController {
    @Autowired
    SettingCrudJpaComponent jpaComponent;

    @GetMapping("/settings/all")
    public List<SettingDto> getAll() {
        return jpaComponent.getAll();
    }

    @PostMapping("/settings/create")
    public void create(HttpServletRequest request) {
        try {
            jpaComponent.create(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/settings/create/batch")
    public void createBatch(HttpServletRequest request) {
        try {
            jpaComponent.createBatch(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
