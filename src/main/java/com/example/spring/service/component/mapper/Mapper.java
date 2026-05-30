package com.example.spring.service.component.mapper;

import org.apache.commons.collections4.map.HashedMap;

import java.util.Hashtable;

public interface Mapper<D, E> {
    D entityToDto(E entity);
}
