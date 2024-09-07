package com.example.spring.component.jpa;

import com.example.spring.model.dto.AccountTypeDto;
import com.example.spring.model.entity.AccountTypeEntity;
import com.example.spring.model.mapper.AccountTypeMapper;
import com.example.spring.repositories.AccountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AccountTypeCrudJpaComponent implements CrudJpaComponent<AccountTypeDto> {

    @Autowired
    AccountTypeMapper mapper;
    @Autowired
    AccountTypeRepository repository;


    @Override
    public List<AccountTypeDto> getAll() {
        ArrayList<AccountTypeDto> dtoList = new ArrayList<>();
        ArrayList<AccountTypeEntity> entityList = new ArrayList<>();
        repository.findAll().forEach(entityList::add);
        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.entityToDto(entityList.get(i)));
        }
        return dtoList;
    }

    @Override
    public AccountTypeDto create(String jsonData) throws IOException {
        AccountTypeDto dto = mapper.stringToDto(jsonData);
        repository.save(mapper.dtoToEntity(dto));
        return dto;
    }

    public List<AccountTypeDto> createBatch(String jsonData) throws IOException {
        List<AccountTypeEntity> entityList = mapper.listStringToListEntity(jsonData);
        repository.saveAll(entityList);
        return mapper.listStringToListDto(jsonData);
    }
}
