package com.example.spring.service.jpa;

import com.example.spring.model.dto.UserDto;
import com.example.spring.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
@Slf4j
public class UserCrudJpaComponentTest extends SpringBootApplicationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCrudJpaComponent component;

    @BeforeEach
    void reload() {
        assertTrue(postgreSQLContainer.isRunning());
        userRepository.deleteAll();
    }

    @Test
    void testInsert() throws Exception {
        component.create(UserDto.builder().
                id(1L).
                fullName("odin").
                totalBalance(1L).
                build());
        assertEquals(userRepository.findById(1L).get().getFullName(), "odin");
        assertEquals(userRepository.findById(1L).get().getTotalBalance(), 1L);
    }

    @Test
    void testInsertBatch() {
        List<UserDto> userDtos = new ArrayList<>();
        userDtos.add(UserDto.builder().
                id(1L).
                fullName("dva").
                totalBalance(1L).
                build());
        userDtos.add(UserDto.builder().
                id(2L).
                fullName("odin").
                totalBalance(1L).
                build());
        userDtos.add(UserDto.builder().
                id(56L).
                fullName("tri").
                totalBalance(1L).
                build());
        component.createBatch(userDtos);
        assertEquals(userRepository.findById(1L).get().getFullName(), "dva");
        assertEquals(userRepository.findById(1L).get().getTotalBalance(), 1L);
        assertEquals(userRepository.findById(2L).get().getFullName(), "odin");
        assertEquals(userRepository.findById(2L).get().getTotalBalance(), 1L);
        assertEquals(userRepository.findById(56L).get().getFullName(), "tri");
        assertEquals(userRepository.findById(56L).get().getTotalBalance(), 1L);
    }


    @Test
    void testGetAll() {
        List<UserDto> userDtos = new ArrayList<>();
        userDtos.add(UserDto.builder().
                id(1L).
                fullName("dva").
                totalBalance(1L).
                build());
        userDtos.add(UserDto.builder().
                id(2L).
                fullName("odin").
                totalBalance(1L).
                build());
        userDtos.add(UserDto.builder().
                id(56L).
                fullName("tri").
                totalBalance(1L).
                build());
        component.createBatch(userDtos);
        userDtos = component.getAll();
        assertEquals(userDtos.get(0).getFullName(), "dva");
        assertEquals(userDtos.get(0).getTotalBalance(), 1L);
        assertEquals(userDtos.get(1).getFullName(), "odin");
        assertEquals(userDtos.get(1).getTotalBalance(), 1L);
        assertEquals(userDtos.get(2).getFullName(), "tri");
        assertEquals(userDtos.get(2).getTotalBalance(), 1L);
    }
}

