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
class OperationValidationTest extends SpringBootApplicationTest {
    @Autowired
    private MockMvc mockMvc;

    /*
    Проверка верной отработки валидации при id < 0
    */
    @Test
    @Transactional
    void operationPositiveIdValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/operations/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": -1,
                                    "fromAccount": 1,
                                    "toAccount": 2,
                                    "sum": 1,
                                    "comment": "testString"
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при fromAccount < 0
    */
    @Test
    @Transactional
    void operationPositiveFromAccountValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/operations/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "fromAccount": -1,
                                    "toAccount": 2,
                                    "sum": 1,
                                    "comment": "testString"
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при toAccount < 0
    */
    @Test
    @Transactional
    void operationPositiveToAccountValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/operations/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "fromAccount": 1,
                                    "toAccount": -2,
                                    "sum": 1,
                                    "comment": "testString"
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при sum < 1
    */
    @Test
    @Transactional
    void operationPositiveSumValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/operations/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "fromAccount": 1,
                                    "toAccount": 2,
                                    "sum": 0,
                                    "comment": "testString"
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при длинне comment более 10 символов
    */
    @Test
    @Transactional
    void operationCommentBiggerLengthValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/operations/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "fromAccount": 1,
                                    "toAccount": 2,
                                    "sum": 1,
                                    "comment": "testStringWithBigLength"
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при длинне comment менее 2 символов
    */
    @Test
    @Transactional
    void operationCommentSmallerLengthValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/operations/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "fromAccount": 1,
                                    "toAccount": 2,
                                    "sum": 1,
                                    "comment": "p"
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при id = null
    */
    @Test
    @Transactional
    void operationNotNullIdValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/operations/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fromAccount": 1,
                                    "toAccount": 2,
                                    "sum": 1,
                                    "comment": "testString"
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при fromAccount = null
    */
    @Test
    @Transactional
    void operationNotNullFromAccountValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/operations/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "toAccount": 2,
                                    "sum": 1,
                                    "comment": "testString"
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при toAccount = null
    */
    @Test
    @Transactional
    void operationNotNullToAccountValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/operations/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": -1,
                                    "fromAccount": 1,
                                    "sum": 1,
                                    "comment": "testString"
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при sum = null
    */
    @Test
    @Transactional
    void operationNotNullSumValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/operations/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": -1,
                                    "fromAccount": 1,
                                    "toAccount": 2,
                                    "comment": "testString"
                                }
                                """))
                .andExpect(status().is(400));
    }

    /*
    Проверка верной отработки валидации при comment = null
    */
    @Test
    @Transactional
    void operationNotNullCommentValidationTest() throws Exception {
        mockMvc.perform(post("/jpa/operations/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": -1,
                                    "fromAccount": 1,
                                    "toAccount": 2,
                                    "sum": 1,
                                    "comment": "testString"
                                }
                                """))
                .andExpect(status().is(400));
    }
}
