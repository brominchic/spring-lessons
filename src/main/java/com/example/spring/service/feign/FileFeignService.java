package com.example.spring.service.feign;

import com.example.spring.clients.FileClient;
import com.example.spring.model.entity.FileEntity;
import com.example.spring.repositories.FileRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FileFeignService {
    @Autowired
    FileClient fileClient;

    @Autowired
    FileRepository repository;

    @Transactional
    public Long create(String path) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(path, this.getClass().getClassLoader());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", classPathResource);
        body.add("userId", 1);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        return fileClient.uploadFile(requestEntity);
    }

    @Transactional
    public HttpEntity<byte[]> getFile(Long id, HttpServletResponse response) {
        FileEntity file = repository.findById(id).orElseThrow();
        byte[] resource = file.getFileData();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getFilename());
        return new HttpEntity<>(resource, headers);
    }
}
