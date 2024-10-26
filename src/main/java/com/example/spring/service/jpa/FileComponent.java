package com.example.spring.service.jpa;

import com.example.spring.model.entity.FileEntity;
import com.example.spring.model.entity.UserEntity;
import com.example.spring.repositories.FileRepository;
import lombok.RequiredArgsConstructor;
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
        FileEntity entity = FileEntity.
                builder().
                userEntity(UserEntity.builder().id(userId).build()).
                fileData(file.getBytes()).
                filename(file.getOriginalFilename()).
                build();
        return repository.save(entity).getId();
    }
    
    @Transactional
    public byte[] getFile(Long id) throws IOException {
        byte[] resource = repository.findById(id).orElseThrow().getFileData();
        return resource;
    }
}
