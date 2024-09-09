package com.example.spring.service.jpa;

import com.example.spring.model.dto.OperationDto;
import com.example.spring.model.entity.OperationEntity;
import com.example.spring.repositories.OperationRepository;
import com.example.spring.service.component.mapper.OperationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OperationCrudJpaComponent implements CrudJpaComponent<OperationDto> {

    private final OperationMapper mapper;
    private final OperationRepository repository;


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
    public OperationDto create(OperationDto dto) throws IOException {
        return mapper.entityToDto(repository.save(mapper.dtoToEntity(dto)));
    }

    @Override
    public List<OperationDto> createBatch(List<OperationDto> dList) {
        List<OperationEntity> entityList = new ArrayList<>();
        for (OperationDto dto : dList) {
            entityList.add(mapper.dtoToEntity(dto));
        }
        List<OperationDto> result = new ArrayList<>();
        for (OperationEntity entity : repository.saveAll(entityList)) {
            result.add(mapper.entityToDto(entity));
        }
        return result;
    }
}
