package com.example.spring.service.component.mapper;

import com.example.spring.model.dto.SettingDto;
import com.example.spring.model.entity.SettingEntity;
import org.springframework.stereotype.Component;

@Component
public class SettingMapper implements Mapper<SettingDto, SettingEntity> {

    @Override
    public SettingDto entityToDto(SettingEntity settingEntity) {
        return SettingDto.builder().
                id(settingEntity.getId()).
                name(settingEntity.getName()).
                build();
    }

    @Override
    public SettingEntity dtoToEntity(SettingDto settingDto) {
        return SettingEntity.builder().
                id(settingDto.getId()).
                name(settingDto.getName()).
                build();
    }

}
