package com.example.spring.component.jpa;

import com.example.spring.model.dto.OperationDto;
import com.example.spring.model.entity.OperationEntity;
import com.example.spring.model.mapper.OperationMapper;
import com.example.spring.repositories.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class OperationCrudJpaComponent implements CrudJpaComponent<OperationDto> {

    @Autowired
    OperationMapper mapper;
    @Autowired
    OperationRepository repository;


    @Override
    public List<OperationDto> getAll() {
        ArrayList<OperationDto> dtoList = new ArrayList<>();
        List<OperationEntity> entityList = new ArrayList<>();
        repository.findAll().forEach(entityList::add);
        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.entityToDto(entityList.get(i)));
        }
        return dtoList;

    }

    @Override
    public OperationDto create(String jsonData) throws IOException {
        OperationDto dto = mapper.stringToDto(jsonData);
        repository.save(mapper.dtoToEntity(dto));
        return dto;
    }

    public List<OperationDto> createBatch(String jsonData) throws IOException {
        List<OperationEntity> entityList = mapper.listStringToListEntity(jsonData);
        repository.saveAll(entityList);
        return mapper.listStringToListDto(jsonData);
    }
}
