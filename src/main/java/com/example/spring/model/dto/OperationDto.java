package com.example.spring.model.dto;

import com.example.spring.model.entity.SettingEntity;

import java.util.HashSet;
import java.util.Set;

public class OperationDto {

    private final Set<SettingEntity> settings = new HashSet<SettingEntity>();
    private Long id;
    private Long fromAccount;
    private Long toAccount;
    private Long sum;
    private String comment;

}
