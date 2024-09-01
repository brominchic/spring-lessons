package com.example.spring.repositories;

import com.example.spring.model.entity.SettingEntity;
import org.springframework.data.repository.CrudRepository;

public interface SettingRepository extends CrudRepository<SettingEntity, Long> {
}