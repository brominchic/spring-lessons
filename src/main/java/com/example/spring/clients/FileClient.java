package com.example.spring.clients;

import com.example.spring.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "FileClient", url = "http://localhost:9999/example-application/jpa/files", configuration = FeignConfig.class)
public interface FileClient {
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Long uploadFile(@RequestPart("file") MultipartFile file, @RequestPart("userId") Integer Id);

    @GetMapping("/download/{id}")
    HttpEntity<byte[]> getFile(@PathVariable Long id);
}