package com.example.spring.service.component.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface Mapper<D, E> {
    D entityToDto(E entity) throws IOException;

    E dtoToEntity(D dto);

    D stringToDto(String string) throws JsonProcessingException;

}
