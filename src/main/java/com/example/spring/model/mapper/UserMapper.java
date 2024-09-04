package com.example.spring.model.mapper;

import com.example.spring.model.dto.UserDto;
import com.example.spring.model.entity.UserEntity;
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
        return new UserEntity(userDto.getId(), userDto.getFullName(), userDto.getTotalBalance());
    }
}
