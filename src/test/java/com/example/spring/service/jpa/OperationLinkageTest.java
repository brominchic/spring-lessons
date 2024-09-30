package com.example.spring.service.jpa;

import com.example.spring.model.entity.*;
import com.example.spring.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
@Slf4j
@AutoConfigureMockMvc
class OperationLinkageTest extends SpringBootApplicationTest {

    @Autowired
    private OperationRepository operationRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountTypeRepository accountTypeRepository;
    @Autowired
    private SettingRepository settingRepository;


    @Test
    @Transactional
    void oneSettingLinkageTest() {
        System.out.println();
        AccountTypeEntity accountTypeEntity = AccountTypeEntity.builder().
                name(2L).
                id(1L).
                build();
        accountTypeRepository.save(accountTypeEntity);
        UserEntity user = UserEntity.builder().
                id(1L).
                fullName("brom").
                totalBalance(1L).
                build();
        userRepository.save(user);
        AccountEntity firtsAccountEntity = AccountEntity.builder().
                userEntity(user).
                accountTypeEntity(accountTypeEntity).
                number(1L).
                balance(0L).
                build();
        AccountEntity secondAccountEntity = AccountEntity.builder().
                userEntity(user).
                accountTypeEntity(accountTypeEntity).
                number(2L).
                balance(0L).
                build();
        accountRepository.save(firtsAccountEntity);
        accountRepository.save(secondAccountEntity);
        SettingEntity settingEntity = SettingEntity.builder().
                id(1L).
                name("ssss").
                build();
        settingRepository.save(settingEntity);
        Set<SettingEntity> settingEntities = new HashSet<>();
        settingEntities.add(settingEntity);
        OperationEntity operationEntity = OperationEntity.builder()
                .id(1L)
                .sum(1L)
                .settings(settingEntities)
                .toAccount(firtsAccountEntity)
                .fromAccount(secondAccountEntity)
                .comment("dddd")
                .build();
        operationRepository.save(operationEntity);
        var settings = operationRepository.findById(1L).get().getSettings();
        assertEquals(settings, settingEntities);

    }

    @Test
    @Transactional
    void manySettingLinkageTest() {
        System.out.println();
        AccountTypeEntity accountTypeEntity = AccountTypeEntity.builder().
                name(2L).
                id(1L).build();
        accountTypeRepository.save(accountTypeEntity);
        UserEntity user = UserEntity.builder().
                id(1L).
                fullName("brom").
                totalBalance(1L).
                build();
        userRepository.save(user);
        AccountEntity firtsAccountEntity = AccountEntity.builder().
                userEntity(user).
                accountTypeEntity(accountTypeEntity).
                number(1L).
                balance(0L).
                build();
        AccountEntity secondAccountEntity = AccountEntity.builder().
                userEntity(user).
                accountTypeEntity(accountTypeEntity).
                number(2L).
                balance(0L).
                build();
        accountRepository.save(firtsAccountEntity);
        accountRepository.save(secondAccountEntity);
        Set<SettingEntity> settingEntities = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            SettingEntity settingEntity = SettingEntity.builder().
                    id(1L).
                    name("ssss").
                    build();
            settingRepository.save(settingEntity);
            settingEntities.add(settingEntity);
        }
        OperationEntity operationEntity = OperationEntity.builder()
                .id(1L)
                .sum(1L)
                .settings(settingEntities)
                .toAccount(firtsAccountEntity)
                .fromAccount(secondAccountEntity)
                .comment("dddd")
                .build();
        operationRepository.save(operationEntity);
        var settings = operationRepository.findById(1L).get().getSettings();
        assertEquals(settings, settingEntities);

    }

    @Test
    @Transactional
    void accountsLinkageTest() {
        System.out.println();
        AccountTypeEntity accountTypeEntity = AccountTypeEntity.builder().
                name(2L).
                id(1L).build();
        accountTypeRepository.save(accountTypeEntity);
        UserEntity user = UserEntity.builder().
                id(1L).
                fullName("brom").
                totalBalance(1L).
                build();
        userRepository.save(user);
        AccountEntity firtsAccountEntity = AccountEntity.builder().
                userEntity(user).
                accountTypeEntity(accountTypeEntity).
                number(1L).
                balance(0L).
                build();
        AccountEntity secondAccountEntity = AccountEntity.builder().
                userEntity(user).
                accountTypeEntity(accountTypeEntity).
                number(2L).
                balance(0L).
                build();
        accountRepository.save(firtsAccountEntity);
        accountRepository.save(secondAccountEntity);
        SettingEntity settingEntity = SettingEntity.builder().
                id(1L).
                name("ssss").
                build();
        settingRepository.save(settingEntity);
        Set<SettingEntity> settingEntities = new HashSet<>();
        settingEntities.add(settingEntity);
        OperationEntity operationEntity = OperationEntity.builder()
                .id(1L)
                .sum(1L)
                .settings(settingEntities)
                .toAccount(firtsAccountEntity)
                .fromAccount(secondAccountEntity)
                .comment("dddd")
                .build();
        operationRepository.save(operationEntity);
        operationEntity = operationRepository.findById(1L).get();
        assertEquals(operationEntity.getToAccount(), firtsAccountEntity);
        assertEquals(operationEntity.getFromAccount(), secondAccountEntity);

    }

}
