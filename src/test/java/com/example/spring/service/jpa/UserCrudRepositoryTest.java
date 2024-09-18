package com.example.spring.service.jpa;

import com.example.spring.controller.UserCrudJpaController;
import com.example.spring.model.entity.UserEntity;
import com.example.spring.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
@Slf4j
public class UserCrudRepositoryTest extends SpringBootApplicationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCrudJpaController controller;

    @BeforeEach
    void reload() {
        assertTrue(postgreSQLContainer.isRunning());
        userRepository.deleteAll();
    }

    @Test
    void testInsert() throws Exception {
        userRepository.save(UserEntity.builder().
                id(1L).
                fullName("odin").
                totalBalance(1L).
                build());
        assertEquals(userRepository.findById(1L).get().getFullName(), "odin");
        assertEquals(userRepository.findById(1L).get().getTotalBalance(), 1L);
    }

    @Test
    void testInsertBatch() {
        ArrayList<UserEntity> userEntities = new ArrayList<>();
        userEntities.add(UserEntity.builder().
                id(1L).
                fullName("dva").
                totalBalance(1L).
                build());
        userEntities.add(UserEntity.builder().
                id(2L).
                fullName("odin").
                totalBalance(1L).
                build());
        userEntities.add(UserEntity.builder().
                id(56L).
                fullName("tri").
                totalBalance(1L).
                build());
        userRepository.saveAll(userEntities);
        assertEquals(userRepository.findById(1L).get().getFullName(), "dva");
        assertEquals(userRepository.findById(1L).get().getTotalBalance(), 1L);
        assertEquals(userRepository.findById(2L).get().getFullName(), "odin");
        assertEquals(userRepository.findById(2L).get().getTotalBalance(), 1L);
        assertEquals(userRepository.findById(56L).get().getFullName(), "tri");
        assertEquals(userRepository.findById(56L).get().getTotalBalance(), 1L);
    }

    @Test
    void testDeleteAll() {
        userRepository.save(UserEntity.builder().
                id(1L).
                fullName("odin").
                totalBalance(1L).
                build());
        assertEquals(userRepository.count(), 1);
        assertTrue(postgreSQLContainer.isRunning());
        userRepository.deleteAll();
        assertEquals(userRepository.count(), 0);
    }

    @Test
    void testCount() {
        assertEquals(userRepository.count(), 0);
        userRepository.save(UserEntity.builder().
                id(1L).
                fullName("odin").
                totalBalance(1L).
                build());
        assertEquals(userRepository.count(), 1);
    }

    @Test
    void testDeleteEntity() {
        userRepository.save(UserEntity.builder().
                id(1L).
                fullName("odin").
                totalBalance(1L).
                build());
        assertEquals(userRepository.count(), 1);
        userRepository.delete(UserEntity.builder().
                id(1L).
                fullName("odin").
                totalBalance(1L).
                build());
        assertEquals(userRepository.count(), 0);
    }

    @Test
    void testExist() {
        userRepository.save(UserEntity.builder().
                id(1L).
                fullName("odin").
                totalBalance(1L).
                build());
        assertTrue(userRepository.existsById(1L));
    }
}

