package com.example.spring.service.jpa;

import com.example.spring.model.dto.SettingDto;
import com.example.spring.model.entity.SettingEntity;
import com.example.spring.repositories.SettingRepository;
import com.example.spring.service.component.mapper.SettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SettingCrudJpaComponent implements CrudJpaComponent<SettingDto> {

    @Autowired
    SettingMapper mapper;
    @Autowired
    SettingRepository repository;


    @Override
    public List<SettingDto> getAll() {
        ArrayList<SettingDto> dtoList = new ArrayList<>();
        ArrayList<SettingEntity> entityList = new ArrayList<>();
        repository.findAll().forEach(entityList::add);
        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.entityToDto(entityList.get(i)));
        }
        return dtoList;
    }

    @Override
    @Transactional
    public SettingDto create(SettingDto dto) throws IOException {
        return mapper.entityToDto(repository.save(mapper.dtoToEntity(dto)));
    }

    @Override
    @Transactional
    public List<SettingDto> createBatch(List<SettingDto> dList) {
        List<SettingEntity> entityList = new ArrayList<>();
        for (SettingDto dto : dList) {
            entityList.add(mapper.dtoToEntity(dto));
        }
        List<SettingDto> result = new ArrayList<>();
        for (SettingEntity entity : repository.saveAll(entityList)) {
            result.add(mapper.entityToDto(entity));
        }
        return result;
    }
}
