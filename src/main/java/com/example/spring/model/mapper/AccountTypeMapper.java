package com.example.spring.model.mapper;

import com.example.spring.model.dto.AccountTypeDto;
import com.example.spring.model.entity.AccountTypeEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

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
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(string, AccountTypeDto.class);
    }
}
