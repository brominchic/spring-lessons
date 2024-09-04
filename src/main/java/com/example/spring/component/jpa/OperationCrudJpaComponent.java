package com.example.spring.component.jpa;

import com.example.spring.model.dto.OperationDto;
import com.example.spring.model.entity.OperationEntity;
import com.example.spring.model.mapper.OperationMapper;
import com.example.spring.repositories.OperationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class OperationCrudJpaComponent implements CrudJpaComponent<OperationDto> {

    @Autowired
    OperationMapper mapper;
    @Autowired
    OperationRepository repository;


    @Override
    public List<OperationDto> getAll() {
        ArrayList<OperationDto> dtoList = new ArrayList<>();
        ArrayList<OperationEntity> entityList = new ArrayList<>();
        repository.findAll().forEach(entityList::add);
        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.entityToDto(entityList.get(i)));
        }
        return dtoList;
    }

    @Override
    public void create(OperationDto operationDto) {
        repository.save(mapper.dtoToEntity(operationDto));
    }

    @Override
    public void createBatch(HttpServletRequest request) throws IOException {
        Scanner scanner = new Scanner(request.getInputStream(), StandardCharsets.UTF_8);
        String jsonData = scanner.useDelimiter("\\A").next();
        scanner.close();
        ObjectMapper objectMapper = new ObjectMapper();
        List<OperationDto> dtoList = objectMapper.readValue(jsonData, new TypeReference<>() {
        });
        for (int i = 0; i < dtoList.size(); i++) {
            repository.save(mapper.dtoToEntity(dtoList.get(i)));
        }
    }
}
