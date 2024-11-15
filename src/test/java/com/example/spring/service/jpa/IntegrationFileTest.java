package com.example.spring.service.jpa;

import com.example.spring.model.entity.UserEntity;
import com.example.spring.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.shaded.com.google.common.io.Files;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class IntegrationFileTest extends SpringBootApplicationTest {
    @LocalServerPort
    private final Integer port = 9999;
    @Autowired
    UserRepository repository;
    @Autowired
    private RestTemplate restTemplate;

    @Test
    void testUploadAndDownloadFile() throws Exception {
        // Загрузка пользователя в бд
        UserEntity userEntity = UserEntity.builder().
                id(1L).
                fullName("dva").
                totalBalance(100L).
                build();
        repository.save(userEntity);
        // URL
        String uploadUrl = "http://localhost:" + port + "/example-application/jpa/files/upload";
        String downloadUrl = "http://localhost:" + port + "/example-application/jpa/files/download/1";
        // Отправка запроса
        File file = new File("src/test/resources/antonosov.txt");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));
        body.add("userId", 1);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> uploadResponse = restTemplate.postForEntity(uploadUrl, requestEntity, String.class);
        assertEquals(HttpStatus.OK, uploadResponse.getStatusCode());
        assertEquals("1", uploadResponse.getBody());
        // Скачивание файла
        ResponseEntity<Resource> downloadResponse = restTemplate.getForEntity(downloadUrl, Resource.class);
        assertEquals(HttpStatus.OK, downloadResponse.getStatusCode());
        // Проверка соответствия полученного файла
        byte[] downloadedData = StreamUtils.copyToByteArray(downloadResponse.getBody().getInputStream());
        byte[] originalData = (Files.toByteArray(file));
        assertArrayEquals(originalData, downloadedData);
    }

}

