package com.example.spring.service.component.mapper;

import com.example.spring.model.dto.AccountTypeDto;
import com.example.spring.model.entity.AccountTypeEntity;
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

}
