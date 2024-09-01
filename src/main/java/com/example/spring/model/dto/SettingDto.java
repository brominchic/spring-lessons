package com.example.spring.model.dto;

import com.example.spring.model.entity.OperationMyEntity;
import com.example.spring.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SettingDto {

    private final Set<UserEntity> users = new HashSet<UserEntity>();

    private final Set<OperationMyEntity> operations = new HashSet<OperationMyEntity>();

    private Long id;

    private String name;

}
