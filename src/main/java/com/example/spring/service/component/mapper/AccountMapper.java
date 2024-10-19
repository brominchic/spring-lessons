package com.example.spring.service.component.mapper;

import com.example.spring.model.dto.AccountDto;
import com.example.spring.model.entity.AccountEntity;
import com.example.spring.model.entity.AccountTypeEntity;
import com.example.spring.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountMapper implements Mapper<AccountDto, AccountEntity> {

    @Override
    public AccountDto entityToDto(AccountEntity entity) {
        return AccountDto.builder().
                type(entity.getAccountTypeEntity().getId()).
                userId(entity.getUserEntity().getId()).
                number(entity.getNumber()).
                balance(entity.getBalance()).
                build();
    }

    @Override
    public AccountEntity dtoToEntity(AccountDto dto) {
        return AccountEntity.builder().
                userEntity(UserEntity.builder().id(dto.getUserId()).build()).
                accountTypeEntity(AccountTypeEntity.builder().id(dto.getType()).build()).
                number(dto.getNumber()).
                balance(dto.getBalance()).
                build();
    }

}
