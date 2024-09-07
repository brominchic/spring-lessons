package com.example.spring.component.jpa;

import com.example.spring.model.dto.SettingDto;
import com.example.spring.model.entity.SettingEntity;
import com.example.spring.model.mapper.SettingMapper;
import com.example.spring.repositories.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public SettingDto create(String jsonData) throws IOException {
        SettingDto dto = mapper.stringToDto(jsonData);
        repository.save(mapper.dtoToEntity(dto));
        return dto;
    }

    public List<SettingDto> createBatch(String jsonData) throws IOException {
        List<SettingEntity> entityList = mapper.listStringToListEntity(jsonData);
        repository.saveAll(entityList);
        return mapper.listStringToListDto(jsonData);
    }
}
