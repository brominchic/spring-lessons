package com.example.spring.service.jpa;

import com.example.spring.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RequiredArgsConstructor
@Slf4j
@AutoConfigureMockMvc
public class ValidationTest extends SpringBootApplicationTest {
    @Autowired
    private UserRepository repository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    void createTest() throws Exception {
        mockMvc.perform(post("/jpa/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("""
                        {   
                            "id":1,
                            "fullName":"dvafffffffffffffffff",
                            "totalBalance":100
                        }
                        """));
        var userDto = repository.findById(1L);
        try {
            assertEquals("dvafffffffffffffffff", userDto.get().getFullName());
        } catch (NoSuchElementException e) {
            assertEquals(e.getClass(), NoSuchElementException.class);
        }

    }

}