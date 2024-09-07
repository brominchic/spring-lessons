package com.example.spring.controller;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;


public interface CrudJpaController<D> {

    List getAll();

    D create(HttpServletRequest request) throws IOException;

    List<D> createBatch(HttpServletRequest request);

}
