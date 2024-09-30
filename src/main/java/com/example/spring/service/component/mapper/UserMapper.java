package com.example.spring.service.component.mapper;

import com.example.spring.config.ApplicationConfig;
import com.example.spring.model.dto.AccountDto;
import com.example.spring.model.dto.UserDto;
import com.example.spring.model.dto.UserWithAccountsDto;
import com.example.spring.model.entity.AccountEntity;
import com.example.spring.model.entity.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMapper implements Mapper<UserDto, UserEntity> {
    private final AccountMapper accountMapper;

    public UserDto entityToDto(UserEntity userEntity) {
        ArrayList<Long> accounts = new ArrayList<>();
        List<AccountEntity> entityList = userEntity.getAccountEntityList();
        if (entityList == null) {
            return UserDto.builder().
                    id(userEntity.getId()).
                    fullName(userEntity.getFullName()).
                    totalBalance(userEntity.getTotalBalance()).
                    build();
        }
        for (int i = 0; i < entityList.size(); i++) {
            accounts.add(entityList.get(i).getNumber());
        }
        return UserDto.builder().
                accounts(accounts).
                id(userEntity.getId()).
                fullName(userEntity.getFullName()).
                totalBalance(userEntity.getTotalBalance()).
                build();

    }

    public UserEntity dtoToEntity(UserDto userDto) {
        return UserEntity.builder().
                id(userDto.getId()).
                fullName(userDto.getFullName()).
                totalBalance(userDto.getTotalBalance()).
                build();
    }

    @Override
    public UserDto stringToDto(String string) throws JsonProcessingException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        ObjectMapper objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
        return objectMapper.readValue(string, UserDto.class);
    }

    @Transactional
    public UserWithAccountsDto entityToDtoWithAccounts(UserEntity userEntity) {
        ArrayList<AccountDto> accounts = new ArrayList<>();
        List<AccountEntity> entityList = userEntity.getAccountEntityList();
        if (entityList == null) {
            return UserWithAccountsDto.builder().
                    id(userEntity.getId()).
                    fullName(userEntity.getFullName()).
                    totalBalance(userEntity.getTotalBalance()).
                    build();
        }
        for (int i = 0; i < entityList.size(); i++) {
            accounts.add(accountMapper.entityToDto(entityList.get(i)));
        }
        return UserWithAccountsDto.builder().
                accounts(accounts).
                id(userEntity.getId()).
                fullName(userEntity.getFullName()).
                totalBalance(userEntity.getTotalBalance()).
                build();

    }
}
