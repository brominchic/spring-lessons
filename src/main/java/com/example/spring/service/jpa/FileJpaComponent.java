package com.example.spring.service.jpa;

import com.example.spring.model.dto.FileDto;
import com.example.spring.repositories.FileRepository;
import com.example.spring.service.component.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FileJpaComponent {

    private final FileMapper mapper;
    private final FileRepository repository;

    @Transactional
    public void create(Long id, Long userId, MultipartFile file) throws IOException {
        FileDto dto = FileDto.builder()
                .id(id)
                .userId(userId)
                .fileData(file)
                .filename(file.getOriginalFilename())
                .build();
        repository.save(mapper.dtoToEntity(dto));
    }


    @Transactional
    public UrlResource getFile(String filename) throws IOException {
        File file = mapper.entityToFile(repository.findByFilename(filename).orElseThrow());
        UrlResource resource = new UrlResource(file.toURI());
        return resource;
    }
}
