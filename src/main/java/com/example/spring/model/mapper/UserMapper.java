package com.example.spring.model.mapper;

import com.example.spring.model.dto.UserDto;
import com.example.spring.model.entity.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

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
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(string, UserDto.class);
    }
}
