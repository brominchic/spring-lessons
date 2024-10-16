package com.example.spring.service.component.mapper;

import com.example.spring.model.dto.FileDto;
import com.example.spring.model.entity.FileEntity;
import com.example.spring.model.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class FileMapper {

    public File entityToFile(FileEntity entity) throws IOException {
        return writeByte(entity.getFileData(), entity.getFilename());

    }

    public FileEntity dtoToEntity(FileDto dto) {
        try {
            FileEntity fileEntity = FileEntity.builder().filename(dto.getFilename()).id(dto.getId()).fileData(dto.getFileData().getBytes()).userEntity(UserEntity.builder().id(dto.getUserId()).build()).build();
            return fileEntity;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File writeByte(byte[] bytes, String filename) throws IOException {
        File file = new File("src/main/resources/files/" + filename);
        try {
            OutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }


}
