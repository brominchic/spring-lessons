package com.example.spring.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


public interface CrudJpaController {

    @GetMapping("/{entityName}/all")
    List getAll();

    @PostMapping("/{entityName}/create")
    void create(HttpServletRequest request);

    @PostMapping("/{entityName}/create/batch")
    void createBatch(HttpServletRequest request);

}
