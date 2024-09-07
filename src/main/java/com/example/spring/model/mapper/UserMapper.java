package com.example.spring.model.mapper;

import com.example.spring.config.ApplicationConfig;
import com.example.spring.model.dto.UserDto;
import com.example.spring.model.entity.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper implements Mapper<UserDto, UserEntity> {
    public UserDto entityToDto(UserEntity userEntity) {
        return UserDto.builder().
                id(userEntity.getId()).
                fullName(userEntity.getFullName()).
                totalBalance(userEntity.getTotalBalance()).
                build();
    }

    public UserEntity dtoToEntity(UserDto userDto) {
        return UserEntity.builder().
                id(userDto.getId()).
                fullName(userDto.getFullName()).
                totalBalance(userDto.getTotalBalance()).
                build();
    }

    @Override
    public UserDto stringToDto(String string) throws JsonProcessingException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        ObjectMapper objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
        return objectMapper.readValue(string, UserDto.class);
    }

    @Override
    public List<UserDto> listStringToListDto(String string) throws JsonProcessingException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        ObjectMapper objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
        List<UserDto> dtoList = objectMapper.readValue(string, new TypeReference<>() {
        });
        return dtoList;
    }

    @Override
    public List<UserEntity> listStringToListEntity(String string) throws JsonProcessingException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        ObjectMapper objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
        List<UserDto> dtoList = objectMapper.readValue(string, new TypeReference<>() {
        });
        List<UserEntity> entityList = new ArrayList<>();
        for (int i = 0; i < dtoList.size(); i++) {
            entityList.add(this.dtoToEntity(dtoList.get(i)));
        }
        return entityList;
    }
}
