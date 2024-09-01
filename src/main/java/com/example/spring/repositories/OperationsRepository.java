package com.example.spring.repositories;

import com.example.spring.model.entity.OperationMyEntity;
import org.springframework.data.repository.CrudRepository;

public interface OperationsRepository extends CrudRepository<OperationMyEntity, Long> {
}
