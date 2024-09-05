package com.example.spring.model.mapper;

import com.example.spring.model.dto.SettingDto;
import com.example.spring.model.entity.SettingEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

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
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(string, SettingDto.class);
    }
}
