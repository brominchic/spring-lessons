package com.example.spring.service.jpa;

import com.example.spring.model.entity.FileEntity;
import com.example.spring.model.entity.UserEntity;
import com.example.spring.repositories.FileRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FileComponent {

    private final FileRepository repository;

    @Transactional
    public Long create(Long userId, MultipartFile file) throws IOException {
        FileEntity entity = FileEntity.builder().userEntity(UserEntity.builder().id(userId).build()).fileData(file.getBytes()).filename(file.getOriginalFilename()).build();
        return repository.save(entity).getId();
    }

    @Transactional
    public HttpEntity<byte[]> getFile(Long id, HttpServletResponse response) throws IOException {
        FileEntity file = repository.findById(id).orElseThrow();
        byte[] resource = file.getFileData();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getFilename());
        return new HttpEntity<byte[]>(resource, headers);
    }
}
