package com.example.spring.service.component.mapper;

import com.example.spring.config.ApplicationConfig;
import com.example.spring.model.dto.AccountDto;
import com.example.spring.model.entity.AccountEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

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

}
