package com.example.spring.service.jpa;

import com.example.spring.clients.FileClient;
import com.example.spring.model.entity.UserEntity;
import com.example.spring.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableFeignClients
class FeignIntegrationFileTest extends SpringBootApplicationTest {
    @LocalServerPort
    private final Integer port = 9999;
    @Autowired
    UserRepository repository;
    @Autowired
    private FileClient client;
    @Value("classpath:antonosov.txt")
    private Resource classPathResource;

    /*
    за интеграционный тест загрузить файл в БД используя feign,
    скачать обратно , проверить соответствие.
    файл должен храниться в resource
    */
    @Test
    void testUploadAndDownloadFile() throws Exception {
        UserEntity userEntity = UserEntity.builder().
                id(1L).
                fullName("dva").
                totalBalance(100L).
                build();
        repository.save(userEntity);
        String name = "antonosov.txt";
        String originalFileName = "antonosov.txt";
        String contentType = "text/plain";
        byte[] content = classPathResource.getContentAsByteArray();
        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);
        // Отправка файла
        client.uploadFile(result, 1);
        // Получение файла
        HttpEntity<byte[]> request = client.getFile(1L);
        assertArrayEquals(content, request.getBody());
    }

}
