package com.example.spring.component.jpa;

import com.example.spring.model.dto.UserDto;
import com.example.spring.model.entity.UserEntity;
import com.example.spring.model.mapper.UserMapper;
import com.example.spring.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserCrudJpaComponent implements CrudJpaComponent<UserDto> {

    @Autowired
    UserMapper mapper;
    @Autowired
    UserRepository repository;


    @Override
    public List<UserDto> getAll() {
        ArrayList<UserDto> dtoList = new ArrayList<>();
        ArrayList<UserEntity> entityList = new ArrayList<>();
        repository.findAll().forEach(entityList::add);
        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.entityToDto(entityList.get(i)));
        }
        return dtoList;
    }

    @Override
    public UserDto create(String jsonData) throws IOException {
        UserDto dto = mapper.stringToDto(jsonData);
        repository.save(mapper.dtoToEntity(dto));
        return dto;
    }

    public List<UserDto> createBatch(String jsonData) throws IOException {
        List<UserEntity> entityList = mapper.listStringToListEntity(jsonData);
        repository.saveAll(entityList);
        return mapper.listStringToListDto(jsonData);
    }
}
