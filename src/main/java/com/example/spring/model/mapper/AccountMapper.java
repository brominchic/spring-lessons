package com.example.spring.model.mapper;

import com.example.spring.config.ApplicationConfig;
import com.example.spring.model.dto.AccountDto;
import com.example.spring.model.entity.AccountEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccountMapper implements Mapper<AccountDto, AccountEntity> {
    @Override
    public AccountDto entityToDto(AccountEntity entity) {
        return AccountDto.builder().
                type(entity.getType()).
                number(entity.getNumber()).
                balance(entity.getBalance()).
                build();
    }

    @Override
    public AccountEntity dtoToEntity(AccountDto dto) {
        return AccountEntity.builder().
                type(dto.getType()).
                number(dto.getNumber()).
                balance(dto.getBalance()).
                build();
    }

    @Override
    public AccountDto stringToDto(String string) throws JsonProcessingException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        ObjectMapper objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
        return objectMapper.readValue(string, AccountDto.class);
    }

    @Override
    public List<AccountDto> listStringToListDto(String string) throws JsonProcessingException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        ObjectMapper objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
        List<AccountDto> dtoList = objectMapper.readValue(string, new TypeReference<>() {
        });
        return dtoList;
    }

    @Override
    public List<AccountEntity> listStringToListEntity(String string) throws JsonProcessingException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        ObjectMapper objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
        List<AccountDto> dtoList = objectMapper.readValue(string, new TypeReference<>() {
        });
        List<AccountEntity> entityList = new ArrayList<>();
        for (int i = 0; i < dtoList.size(); i++) {
            entityList.add(this.dtoToEntity(dtoList.get(i)));
        }
        return entityList;
    }

}
