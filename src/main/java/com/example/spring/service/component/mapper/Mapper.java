package com.example.spring.service.component.mapper;

import org.springframework.stereotype.Service;

@Service
public interface Mapper<D, E> {
    D entityToDto(E entity) throws Exception;

    E dtoToEntity(D dto);

    D stringToDto(String string) throws Exception;

}
