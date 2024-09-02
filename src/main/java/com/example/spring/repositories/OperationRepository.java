package com.example.spring.repositories;

import com.example.spring.model.entity.OperationEntity;
import org.springframework.data.repository.CrudRepository;


public interface OperationRepository extends CrudRepository<OperationEntity, Long> {
}
