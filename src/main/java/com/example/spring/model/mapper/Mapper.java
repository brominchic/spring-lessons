package com.example.spring.model.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface Mapper<D, E> {
    D entityToDto(E entity);

    E dtoToEntity(D dto);

    D stringToDto(String string) throws JsonProcessingException;

    List<D> listStringToListDto(String string) throws JsonProcessingException;

    List<E> listStringToListEntity(String string) throws JsonProcessingException;
}
