package com.example.spring.clients;

import com.example.spring.config.FeignConfig;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "FileClient",
        url = "http://localhost:9999/example-application/jpa/files",
        configuration = FeignConfig.class
)
public interface FileClient {
    @PostMapping("/upload")
    Long uploadFile(HttpEntity entity);

    @GetMapping("/download/{id}")
    HttpEntity<byte[]> getFile(@PathVariable Long id, HttpServletResponse response);
}