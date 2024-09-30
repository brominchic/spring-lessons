package com.example.spring.service.jpa;

import com.example.spring.model.entity.SettingEntity;
import com.example.spring.model.entity.UserEntity;
import com.example.spring.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ManyToManyTest extends SpringBootApplicationTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    @Transactional
    void manyToManyTest() {
        userRepository.save(
                UserEntity.builder()
                        .id(1L)
                        .settings(
                                Set.of(
                                        SettingEntity.builder()
                                                .id(1L)
                                                .name("test")
                                                .build(),
                                        SettingEntity.builder()
                                                .id(2L)
                                                .name("test2")
                                                .build(),
                                        SettingEntity.builder()
                                                .id(3L)
                                                .name("test3")
                                                .build()
                                )
                        )
                        .build()
        );
        var test = userRepository.findUserById(1L);
        assertEquals(test.getSettings().size(), 3);

    }
}
