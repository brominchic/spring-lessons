package com.example.spring.repositories.marketing;

import com.example.spring.model.entity.marketing.FarmerActionStateEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FarmerActionStateRepository extends CrudRepository<FarmerActionStateEntity, Long> {

    List<FarmerActionStateEntity> findByScopeKeyOrderByUpdatedAtDesc(String scopeKey);

    Optional<FarmerActionStateEntity> findByScopeKeyAndActionKey(String scopeKey, String actionKey);
}
