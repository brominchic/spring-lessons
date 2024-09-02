package com.example.spring.repositories;

import com.example.spring.model.entity.AccountEntity;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<AccountEntity, Long> {
}
