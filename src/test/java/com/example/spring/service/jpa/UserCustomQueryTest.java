package com.example.spring.service.jpa;

import com.example.spring.model.dto.AccountDto;
import com.example.spring.model.dto.AccountTypeDto;
import com.example.spring.model.dto.UserDto;
import com.example.spring.model.dto.UserWithAccountsDto;
import com.example.spring.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
@Slf4j
class UserCustomQueryTest extends SpringBootApplicationTest {
    @Autowired
    private UserCrudJpaComponent userComponent;
    @Autowired
    private AccountCrudJpaComponent accountComponent;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountTypeCrudJpaComponent accountTypeComponent;


    @Test
    @Transactional
    void getCustomTest() throws Exception {
        UserDto user = UserDto.builder().
                id(1L).
                fullName("brom").
                totalBalance(0L).
                build();
        userComponent.create(user);
        AccountTypeDto accountTypeDto = AccountTypeDto.builder().
                name(2222L).
                id(1L).
                build();
        accountTypeComponent.create(accountTypeDto);
        AccountDto firtsAccountDto = AccountDto.builder().
                number(1L).
                type(1L).
                balance(0L).
                userId(1L).
                build();
        AccountDto secondAccountDto = AccountDto.builder().
                number(2L).
                type(1L).
                balance(0L).
                userId(1L).
                build();
        accountComponent.create(firtsAccountDto);
        accountComponent.create(secondAccountDto);
        UserWithAccountsDto userDto = userComponent.getByIdWithAccounts(1L);
        assertEquals(firtsAccountDto, userDto.getAccounts().get(0));
        assertEquals(secondAccountDto, userDto.getAccounts().get(1));
        assertEquals(accountTypeDto.getId(), userDto.getAccounts().get(0).getType());
        assertEquals(accountTypeDto.getId(), userDto.getAccounts().get(1).getType());
    }

}