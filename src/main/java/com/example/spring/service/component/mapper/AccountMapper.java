package com.example.spring.service.component.mapper;

import com.example.spring.config.ApplicationConfig;
import com.example.spring.model.dto.AccountDto;
import com.example.spring.model.entity.AccountEntity;
import com.example.spring.repositories.AccountTypeRepository;
import com.example.spring.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountMapper implements Mapper<AccountDto, AccountEntity> {

    private final AccountTypeRepository accountTypeRepository;

    private final UserRepository userRepository;

    @Override
    public AccountDto entityToDto(AccountEntity entity) {
        return AccountDto.builder().
                type(entity.getAccountTypeEntity().getName()).
                userId(entity.getUserEntity().getId()).
                number(entity.getNumber()).
                balance(entity.getBalance()).
                build();
    }

    @Override
    public AccountEntity dtoToEntity(AccountDto dto) {
        return AccountEntity.builder().
                userEntity(userRepository.findById(dto.getUserId()).get()).
                accountTypeEntity(accountTypeRepository.findById(dto.getType()).get()).
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
