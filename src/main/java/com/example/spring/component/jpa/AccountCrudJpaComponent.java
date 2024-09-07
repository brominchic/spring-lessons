package com.example.spring.component.jpa;

import com.example.spring.model.dto.AccountDto;
import com.example.spring.model.entity.AccountEntity;
import com.example.spring.model.mapper.AccountMapper;
import com.example.spring.repositories.AccountRepository;
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
    public AccountDto create(String jsonData) throws IOException {
        AccountDto dto = mapper.stringToDto(jsonData);
        repository.save(mapper.dtoToEntity(dto));
        return dto;
    }

    public List<AccountDto> createBatch(String jsonData) throws IOException {
        List<AccountEntity> entityList = mapper.listStringToListEntity(jsonData);
        repository.saveAll(entityList);
        return mapper.listStringToListDto(jsonData);
    }
}
