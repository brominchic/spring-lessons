package com.example.spring.component.jpa;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public interface CrudJpaComponent<D> {

    List<D> getAll();

    void create(HttpServletRequest request) throws IOException;

    void createBatch(HttpServletRequest request) throws IOException;
}
