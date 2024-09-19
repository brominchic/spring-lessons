package com.example.spring.controller;

import com.example.spring.model.dto.OperationDto;
import com.example.spring.service.jpa.OperationCrudJpaComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/jpa/operations")
@RequiredArgsConstructor
public class OperationCrudJpaController implements CrudJpaController<OperationDto> {

    private final OperationCrudJpaComponent jpaComponent;

    @GetMapping("/all")
    public List<OperationDto> getAll() {
        return jpaComponent.getAll();
    }

    @PostMapping("/create")
    public OperationDto create(@RequestBody OperationDto input) throws IOException {
        return jpaComponent.create(input);
    }

    @PostMapping("/create/batch")
    public List<OperationDto> createBatch(@RequestBody List<OperationDto> input) {
        return jpaComponent.createBatch(input);

    }


}
