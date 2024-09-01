package com.example.spring.model.dto;

import com.example.spring.model.entity.AccountEntity;
import com.example.spring.model.entity.SettingEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;

    private String fullName;

    private Long totalBalance;

    private List<AccountEntity> accountEntityList = new ArrayList<>();

    private Set<SettingEntity> settings = new HashSet<SettingEntity>();
}
