package com.example.spring.service.jpa;

import com.example.spring.model.dto.AccountDto;
import com.example.spring.model.entity.AccountEntity;
import com.example.spring.repositories.AccountRepository;
import com.example.spring.service.component.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AccountCrudJpaComponent implements CrudJpaComponent<AccountDto> {

    @Autowired
    AccountMapper mapper;
    @Autowired
    AccountRepository repository;


    @Override
    public List<AccountDto> getAll() {
        ArrayList<AccountDto> dtoList = new ArrayList<>();
        ArrayList<AccountEntity> entityList = new ArrayList<>();
        repository.findAll().forEach(entityList::add);
        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.entityToDto(entityList.get(i)));
        }
        return dtoList;
    }

    @Override
    public AccountDto create(AccountDto dto) throws IOException {
        return mapper.entityToDto(repository.save(mapper.dtoToEntity(dto)));
    }

    @Override
    public List<AccountDto> createBatch(List<AccountDto> dList) {
        List<AccountEntity> entityList = new ArrayList<>();
        for (AccountDto dto : dList) {
            entityList.add(mapper.dtoToEntity(dto));
        }
        List<AccountDto> result = new ArrayList<>();
        for (AccountEntity entity : repository.saveAll(entityList)) {
            result.add(mapper.entityToDto(entity));
        }
        return result;
    }
}