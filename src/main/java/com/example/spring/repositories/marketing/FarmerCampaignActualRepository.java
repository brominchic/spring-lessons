package com.example.spring.repositories.marketing;

import com.example.spring.model.entity.marketing.FarmerCampaignActualEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FarmerCampaignActualRepository extends CrudRepository<FarmerCampaignActualEntity, Long> {

    List<FarmerCampaignActualEntity> findByScopeKeyOrderByUpdatedAtDesc(String scopeKey);

    Optional<FarmerCampaignActualEntity> findByScopeKeyAndActualKey(String scopeKey, String actualKey);
}
