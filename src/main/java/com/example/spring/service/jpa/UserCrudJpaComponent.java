package com.example.spring.service.jpa;

import com.example.spring.model.dto.UserDto;
import com.example.spring.model.entity.UserEntity;
import com.example.spring.repositories.UserRepository;
import com.example.spring.service.component.mapper.UserMapper;
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
    public UserDto create(UserDto dto) throws IOException {
        return mapper.entityToDto(repository.save(mapper.dtoToEntity(dto)));
    }

    @Override
    public List<UserDto> createBatch(List<UserDto> dList) {
        List<UserEntity> entityList = new ArrayList<>();
        for (UserDto dto : dList) {
            entityList.add(mapper.dtoToEntity(dto));
        }
        List<UserDto> result = new ArrayList<>();
        for (UserEntity entity : repository.saveAll(entityList)) {
            result.add(mapper.entityToDto(entity));
        }
        return result;
    }
}
