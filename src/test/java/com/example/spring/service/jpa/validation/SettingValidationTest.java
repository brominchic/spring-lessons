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
class SettingValidationTest extends SpringBootApplicationTest {
    @Autowired
    private MockMvc mockMvc;

    /*
    Проверка верной отработки валидации при id < 0
    */
    @Test
    @Transactional
    void settingPositiveIdValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": -1,
                                    "name": "testName"
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при длинне name более 10 символов
    */
    @Test
    @Transactional
    void settingNameBiggerLengthValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "name": "testStringWithBigLength"
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при длинне name менее 2 символов
    */
    @Test
    @Transactional
    void settingNameSmallerLengthValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "name": "p"
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при id = null
    */
    @Test
    @Transactional
    void settingNotNullIdValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "testName"
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при name = null
    */
    @Test
    @Transactional
    void settingNotNullNameValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": -1
                                }
                                """))
                .andExpect(status().is(400));
    }
}