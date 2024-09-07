package com.example.spring.model.mapper;

import com.example.spring.config.ApplicationConfig;
import com.example.spring.model.dto.AccountTypeDto;
import com.example.spring.model.entity.AccountTypeEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccountTypeMapper implements Mapper<AccountTypeDto, AccountTypeEntity> {
    @Override
    public AccountTypeDto entityToDto(AccountTypeEntity entity) {
        return AccountTypeDto.builder().
                name(entity.getName()).
                id(entity.getId()).build();
    }

    @Override
    public AccountTypeEntity dtoToEntity(AccountTypeDto dto) {
        return AccountTypeEntity.builder().
                name(dto.getName()).
                id(dto.getId()).build();
    }

    @Override
    public AccountTypeDto stringToDto(String string) throws JsonProcessingException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        ObjectMapper objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
        return objectMapper.readValue(string, AccountTypeDto.class);
    }

    @Override
    public List<AccountTypeDto> listStringToListDto(String string) throws JsonProcessingException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        ObjectMapper objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
        List<AccountTypeDto> dtoList = objectMapper.readValue(string, new TypeReference<>() {
        });
        return dtoList;
    }

    @Override
    public List<AccountTypeEntity> listStringToListEntity(String string) throws JsonProcessingException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        ObjectMapper objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
        List<AccountTypeDto> dtoList = objectMapper.readValue(string, new TypeReference<>() {
        });
        List<AccountTypeEntity> entityList = new ArrayList<>();
        for (int i = 0; i < dtoList.size(); i++) {
            entityList.add(this.dtoToEntity(dtoList.get(i)));
        }
        return entityList;
    }
}
