package com.example.spring.service.jpa;

import com.example.spring.controller.UserCrudJpaController;
import com.example.spring.model.entity.UserEntity;
import com.example.spring.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RequiredArgsConstructor
@Slf4j
@AutoConfigureMockMvc
class UserCrudJpaControllerTest extends SpringBootApplicationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserCrudJpaController controller;
    @Autowired
    private UserRepository repository;

    @Test
    @Transactional
        // откатит все изменения сделанные в тесте
    void getTest() throws Exception {
        List<UserEntity> userEntities = new ArrayList<>();
        userEntities.add(UserEntity.builder().
                id(1L).
                fullName("dva").
                totalBalance(100L).
                build());
        userEntities.add(UserEntity.builder().
                id(2L).
                fullName("odin").
                totalBalance(200L).
                build());
        userEntities.add(UserEntity.builder().
                id(56L).
                fullName("tri").
                totalBalance(300L).
                build());
        repository.saveAll(userEntities);
        mockMvc.perform(get("/jpa/users/all")).
                andDo(print())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].fullName").value("dva"))
                .andExpect(jsonPath("$[0].totalBalance").value("100"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].fullName").value("odin"))
                .andExpect(jsonPath("$[1].totalBalance").value("200"))
                .andExpect(jsonPath("$[2].id").value("56"))
                .andExpect(jsonPath("$[2].fullName").value("tri"))
                .andExpect(jsonPath("$[2].totalBalance").value("300"));
    }

    @Test
    @Transactional
        // откатит все изменения сделанные в тесте
    void createTest() throws Exception {
        mockMvc.perform(post("/jpa/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("""
                        {   
                            "id":1,
                            "fullName":"dva",
                            "totalBalance":100
                        }
                        """));
        var userDto = repository.findById(1L);
        assertEquals("dva", userDto.get().getFullName());
        assertEquals(100L, userDto.get().getTotalBalance());
    }

    @Test
    @Transactional
        // откатит все изменения сделанные в тесте
    void createBatchTest() throws Exception {
        mockMvc.perform(post("/jpa/users/create/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("""
                        [
                            {
                                "id":1,
                                "fullName":"dva",
                                "totalBalance":100
                            },
                                {"id":2,
                                "fullName":"odin",
                                "totalBalance":200
                            },
                            {
                                "id":56,
                                "fullName":"tri",
                                "totalBalance":300
                            }
                        ]
                        """));
        var userDto = repository.findById(1L);
        assertEquals("dva", userDto.get().getFullName());
        assertEquals(100L, userDto.get().getTotalBalance());
        userDto = repository.findById(2L);
        assertEquals("odin", userDto.get().getFullName());
        assertEquals(200L, userDto.get().getTotalBalance());
        userDto = repository.findById(56L);
        assertEquals("tri", userDto.get().getFullName());
        assertEquals(300L, userDto.get().getTotalBalance());
    }

}