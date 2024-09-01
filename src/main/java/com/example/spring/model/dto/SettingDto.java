package com.example.spring.model.dto;

import com.example.spring.model.entity.OperationEntity;
import com.example.spring.model.entity.UserEntity;

import java.util.HashSet;
import java.util.Set;

public class SettingDto {

    private final Set<UserEntity> users = new HashSet<UserEntity>();
    private final Set<OperationEntity> operations = new HashSet<OperationEntity>();
    private Long id;
    private String name;

}
