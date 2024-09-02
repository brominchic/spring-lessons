package com.example.spring.model.mapper;

import org.springframework.stereotype.Service;

@Service
public interface Mapper<D, E> {
    D entityToDto(E entity);

    E dtoToEntity(D dto);
}
