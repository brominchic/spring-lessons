package com.example.spring.model.mapper;

import com.example.spring.model.dto.AccountDto;
import com.example.spring.model.entity.AccountEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(string, AccountDto.class);
    }
}
