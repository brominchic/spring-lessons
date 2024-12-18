package com.example.spring.controller;

import com.example.spring.service.jpa.FileComponent;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/jpa/files")
@RequiredArgsConstructor
public class FileController {

    private final FileComponent fileComponent;

    @PostMapping("/upload")
    public Long uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId) throws IOException {
        return fileComponent.create(userId, file);
    }

    @GetMapping("/download/{id}")
    public HttpEntity<byte[]> getFile(@PathVariable Long id, HttpServletResponse response) throws IOException {
        return fileComponent.getFile(id, response);
    }

}
