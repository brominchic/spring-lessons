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
class UserValidationTest extends SpringBootApplicationTest {
    @Autowired
    private MockMvc mockMvc;

    /*
    Проверка верной отработки валидации при id < 0
    */
    @Test
    @Transactional
    void userPositiveIdValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id":-1,
                                    "fullName":"testName",
                                    "totalBalance":100
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при длинне fullName более 10 символов
    */
    @Test
    @Transactional
    void userNameBiggerLengthValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id":1,
                                    "fullName":"testStringWithBigLength",
                                    "totalBalance":100
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при длинне fullName менее 2 символов
    */
    @Test
    @Transactional
    void userNameSmallerLengthValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id":1,
                                    "fullName":"p",
                                    "totalBalance":100
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при totalBalance < 0
    */
    @Test
    @Transactional
    void userPositiveTotalBalanceValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id":1,
                                    "fullName":"testName",
                                    "totalBalance":-100
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при name = null
    */
    @Test
    @Transactional
    void userNameNotNullValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id":1,
                                    "totalBalance":100
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при balance = null
    */
    @Test
    @Transactional
    void userNotNullTotalBalanceValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id":1,
                                    "fullName":"testName",
                                }
                                """))
                .andExpect(status().is(400));
    }

}
