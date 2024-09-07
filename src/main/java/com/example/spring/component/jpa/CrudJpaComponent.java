package com.example.spring.component.jpa;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public interface CrudJpaComponent<D> {

    List<D> getAll();

    D create(String jsonData) throws IOException;

    List<D> createBatch(String jsonData) throws IOException;
}
