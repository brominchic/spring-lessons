package com.example.spring.controller;

import com.example.spring.model.dto.SettingDto;
import com.example.spring.service.jpa.SettingCrudJpaComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/jpa/settings")
@RequiredArgsConstructor
public class SettingCrudJpaController implements CrudJpaController<SettingDto> {
    @Autowired
    SettingCrudJpaComponent jpaComponent;

    @GetMapping("/all")
    public List<SettingDto> getAll() {
        return jpaComponent.getAll();
    }

    @PostMapping("/create")
    public SettingDto create(@RequestBody SettingDto input) throws IOException {
        return jpaComponent.create(input);
    }

    @PostMapping("/create/batch")
    public List<SettingDto> createBatch(@RequestBody List<SettingDto> input) {
        return jpaComponent.createBatch(input);
    }
}
