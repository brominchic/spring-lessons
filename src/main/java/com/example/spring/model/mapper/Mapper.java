package com.example.spring.model.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public interface Mapper<D, E> {
    D entityToDto(E entity);

    E dtoToEntity(D dto);

    D stringToDto(String string) throws JsonProcessingException;
}
