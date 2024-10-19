package com.example.spring.service.jpa;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public interface CrudJpaComponent<D> {

    List<D> getAll() throws IOException;

    D create(D dto) throws IOException;

    List<D> createBatch(List<D> dList) throws IOException;
}
