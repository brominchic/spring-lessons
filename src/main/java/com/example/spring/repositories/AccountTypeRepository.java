package com.example.spring.repositories;

import com.example.spring.model.entity.AccountTypeEntity;
import org.springframework.data.repository.CrudRepository;

public interface AccountTypeRepository extends CrudRepository<AccountTypeEntity, Long> {
}
