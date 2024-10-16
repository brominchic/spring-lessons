package com.example.spring.controller;

import com.example.spring.service.jpa.FileJpaComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/jpa/files")
@RequiredArgsConstructor
public class FileJpaController {

    private final FileJpaComponent jpaComponent;

    @PostMapping("/upload")
    public void uploadFile(@RequestParam("id") Long id, @RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId) throws IOException {
        jpaComponent.create(id, userId, file);
    }

    @GetMapping("/download/{filename:.+}")
    public UrlResource getFile(@PathVariable String filename) throws IOException {
        return jpaComponent.getFile(filename);
    }

}
