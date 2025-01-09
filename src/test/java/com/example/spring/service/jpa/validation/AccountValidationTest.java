package com.example.spring.service.jpa.validation;

import com.example.spring.service.jpa.SpringBootApplicationTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@Slf4j
@AutoConfigureMockMvc
class AccountValidationTest extends SpringBootApplicationTest {
    @Autowired
    private MockMvc mockMvc;

    /*
    Проверка верной отработки валидации при number < 0
    */
    @Test
    @Transactional
    void AccountPositiveNumberValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "number": -2,
                                    "balance": 2518,
                                    "type": 1,
                                    "userId":2
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при balance < 0
    */
    @Test
    @Transactional
    void AccountPositiveBalanceValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "number": 2,
                                    "balance": -2518,
                                    "type": 1,
                                    "userId":2
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при type < 0
    */
    @Test
    @Transactional
    void AccountPositiveTypeValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "number": 2,
                                    "balance": 2518,
                                    "type": -1,
                                    "userId":2
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при userId < 0
    */
    @Test
    @Transactional
    void AccountPositiveUserIdValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "number": 2,
                                    "balance": 2518,
                                    "type": 1,
                                    "userId":-2
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при number = null
    */
    @Test
    @Transactional
    void AccountNotNullNumberValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "balance": 2518,
                                    "type": 1,
                                    "userId":2
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при balance = null
    */
    @Test
    @Transactional
    void AccountNotNullBalanceValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "number": 2,
                                    "type": 1,
                                    "userId":2
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при type = null
    */
    @Test
    @Transactional
    void AccountNotNullTypeValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "number": 2,
                                    "balance": 2518,
                                    "userId":2
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при userId = null
    */
    @Test
    @Transactional
    void AccountNotNullUserIdValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "number": 2,
                                    "balance": 2518,
                                    "type": 1
                                }
                                """))
                .andExpect(status().is(400));
    }
}
