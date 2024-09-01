package com.example.spring.controller;

import com.example.spring.model.dto.UserDto;
import com.example.spring.model.entity.UserEntity;
import com.example.spring.model.mapper.UserMapper;
import com.example.spring.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/jpa")
@RequiredArgsConstructor
public class TestJpaController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/users/all")
    public Optional<UserEntity> init() {
        UserDto userDto = userMapper.entityToDto(userRepository.findById(Long.valueOf(1)).get());
        return userRepository.findById(Long.valueOf(String.valueOf(userDto)));
    }
}
