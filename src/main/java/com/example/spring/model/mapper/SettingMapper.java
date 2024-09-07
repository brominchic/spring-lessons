package com.example.spring.model.mapper;

import com.example.spring.config.ApplicationConfig;
import com.example.spring.model.dto.SettingDto;
import com.example.spring.model.entity.SettingEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SettingMapper implements Mapper<SettingDto, SettingEntity> {

    public SettingDto entityToDto(SettingEntity settingEntity) {
        return SettingDto.builder().
                id(settingEntity.getId()).
                name(settingEntity.getName()).
                build();
    }

    public SettingEntity dtoToEntity(SettingDto settingDto) {
        return SettingEntity.builder().
                id(settingDto.getId()).
                name(settingDto.getName()).
                build();
    }

    @Override
    public SettingDto stringToDto(String string) throws JsonProcessingException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        ObjectMapper objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
        return objectMapper.readValue(string, SettingDto.class);
    }

    @Override
    public List<SettingDto> listStringToListDto(String string) throws JsonProcessingException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        ObjectMapper objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
        List<SettingDto> dtoList = objectMapper.readValue(string, new TypeReference<>() {
        });
        return dtoList;
    }

    @Override
    public List<SettingEntity> listStringToListEntity(String string) throws JsonProcessingException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        ObjectMapper objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
        List<SettingDto> dtoList = objectMapper.readValue(string, new TypeReference<>() {
        });
        List<SettingEntity> entityList = new ArrayList<>();
        for (int i = 0; i < dtoList.size(); i++) {
            entityList.add(this.dtoToEntity(dtoList.get(i)));
        }
        return entityList;
    }
}
