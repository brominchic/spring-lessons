package com.example.spring.model.mapper;

import com.example.spring.model.dto.SettingDto;
import com.example.spring.model.entity.SettingEntity;
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
        return new SettingEntity(settingDto.getName(), settingDto.getId());
    }
}
