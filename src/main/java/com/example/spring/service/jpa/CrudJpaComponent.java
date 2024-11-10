package com.example.spring.service.jpa;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CrudJpaComponent<D> {

    List<D> getAll();

    D create(D dto);

    List<D> createBatch(List<D> dList);
}
