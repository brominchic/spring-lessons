package com.example.spring.repositories;

import com.example.spring.model.entity.AccountTypesEntity;
import org.springframework.data.repository.CrudRepository;

public interface AccountTypesRepository extends CrudRepository<AccountTypesEntity, Long> {
}
