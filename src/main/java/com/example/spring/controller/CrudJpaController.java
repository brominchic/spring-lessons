package com.example.spring.controller;

import java.io.IOException;
import java.util.List;


public interface CrudJpaController<D> {

    List<D> getAll();

    D create(D input) throws IOException;

    List<D> createBatch(List<D> input);

}
