package com.example.spring.repositories;

import com.example.spring.model.entity.AccountTypesMyEntity;
import org.springframework.data.repository.CrudRepository;

public interface AccountTypesRepository extends CrudRepository<AccountTypesMyEntity, Long> {
}
