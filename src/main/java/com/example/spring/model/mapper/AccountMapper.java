package com.example.spring.model.mapper;

import com.example.spring.model.dto.AccountDto;
import com.example.spring.model.entity.AccountEntity;
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
        return new AccountEntity(dto.getNumber(), dto.getBalance(), dto.getType());
    }
}
