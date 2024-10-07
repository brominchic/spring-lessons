package com.example.spring.service.jpa;

import com.example.spring.model.dto.AccountTypeDto;
import com.example.spring.model.entity.AccountTypeEntity;
import com.example.spring.repositories.AccountTypeRepository;
import com.example.spring.service.component.mapper.AccountTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public AccountTypeDto create(AccountTypeDto dto) throws IOException {
        return mapper.entityToDto(repository.save(mapper.dtoToEntity(dto)));
    }

    @Override
    @Transactional
    public List<AccountTypeDto> createBatch(List<AccountTypeDto> dList) {
        List<AccountTypeEntity> entityList = new ArrayList<>();
        for (AccountTypeDto dto : dList) {
            entityList.add(mapper.dtoToEntity(dto));
        }
        List<AccountTypeDto> result = new ArrayList<>();
        for (AccountTypeEntity entity : repository.saveAll(entityList)) {
            result.add(mapper.entityToDto(entity));
        }
        return result;
    }
}
