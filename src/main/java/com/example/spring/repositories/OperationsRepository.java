package com.example.spring.repositories;

import com.example.spring.model.entity.OperationEntity;
import org.springframework.data.repository.CrudRepository;

public interface OperationsRepository extends CrudRepository<OperationEntity, Long> {
}
