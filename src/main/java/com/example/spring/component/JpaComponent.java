package com.example.spring.component;

import com.example.spring.model.dto.Dto;
import com.example.spring.model.mapper.Mapper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class JpaComponent {

    public ArrayList getAll(Mapper mapper, CrudRepository repository) {
        ArrayList dtoList = new ArrayList<>();
        ArrayList entityList = new ArrayList();
        repository.findAll().forEach(entityList::add);
        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.entityToDto(entityList.get(i)));
        }
        return dtoList;
    }

    public void create(Mapper mapper, CrudRepository repository, Dto dto) {
        System.out.println(dto);
        repository.save(mapper.dtoToEntity(dto));
    }

}
