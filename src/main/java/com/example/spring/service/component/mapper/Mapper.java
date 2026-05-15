package com.example.spring.service.component.mapper;

public interface Mapper<D, E> {
    D entityToDto(E entity);
}
