package com.example.spring.repositories.marketing;

import com.example.spring.model.entity.marketing.FarmerCalendarHistoryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FarmerCalendarHistoryRepository extends CrudRepository<FarmerCalendarHistoryEntity, Long> {

    List<FarmerCalendarHistoryEntity> findTop20ByScopeKeyOrderByCreatedAtDesc(String scopeKey);
}
