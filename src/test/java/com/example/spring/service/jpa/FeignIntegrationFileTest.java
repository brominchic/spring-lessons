package com.example.spring.service.jpa;

import com.example.spring.model.entity.UserEntity;
import com.example.spring.repositories.UserRepository;
import com.example.spring.service.feign.FileFeignService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.io.Resource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableFeignClients
class FeignIntegrationFileTest extends SpringBootApplicationTest {
    @LocalServerPort
    private final Integer port = 9999;
    @Autowired
    UserRepository repository;
    @Autowired
    private FileFeignService service;
    @Value("classpath:antonosov.txt")
    private Resource classPathResource;

    /*
    за интеграционный тест загрузить файл в БД через POST используя restTemplate,
    скачать обратно также через GET, проверить соответствие.
    файл должен храниться в resource
    */
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
        service.create("antonosov.txt");

    }

}
