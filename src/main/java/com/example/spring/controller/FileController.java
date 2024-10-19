package com.example.spring.controller;

import com.example.spring.service.jpa.FileComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/jpa/files")
@RequiredArgsConstructor
public class FileController {

    private final FileComponent jpaComponent;

    @PostMapping("/upload")
    public Long uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId) throws IOException {
        return jpaComponent.create(userId, file);
    }

    @GetMapping("/download/{filename:.+}")
    public byte[] getFile(@PathVariable String filename) throws IOException {
        return jpaComponent.getFile(filename);
    }

}
