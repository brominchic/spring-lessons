package com.example.spring.model.mapper;

import com.example.spring.model.dto.UserDto;
import com.example.spring.model.entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public UserDto entityToDto(UserEntity userEntity) {
        return UserDto.builder().id(userEntity.getId()).fullName(userEntity.getFullName()).settings(userEntity.getSettings()).totalBalance(userEntity.getTotalBalance()).accountEntityList(userEntity.getAccountEntityList()).build();
    }

    public UserEntity dtoToEntity(UserDto userDto) {
        return new UserEntity(userDto.getId(), userDto.getFullName(), userDto.getTotalBalance(), userDto.getAccountEntityList(), userDto.getSettings());
    }
}
