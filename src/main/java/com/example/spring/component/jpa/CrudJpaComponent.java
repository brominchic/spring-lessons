package com.example.spring.component.jpa;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public interface CrudJpaComponent<D> {

    List<D> getAll();

    void create(D dto);

    void createBatch(HttpServletRequest request) throws IOException;
}
