package com.example.spring.service.jpa;

import com.example.spring.model.entity.SettingEntity;
import com.example.spring.model.entity.UserEntity;
import com.example.spring.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
public class ManyToManyTest extends SpringBootApplicationTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    @Transactional
    void name() {

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

        log.info("and then go");
        var test = userRepository.findUserById(1L);

//        var example = userRepository.findById(1L).orElseThrow();

        System.out.println(1);
//        assertEquals(example.getSettings().size(), 3);

    }
}
