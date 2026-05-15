package com.example.spring.repositories.marketing;

import com.example.spring.model.entity.marketing.FarmerPromotionSettingEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FarmerPromotionSettingRepository extends CrudRepository<FarmerPromotionSettingEntity, Long> {

    List<FarmerPromotionSettingEntity> findByScopeKeyOrderByUpdatedAtDesc(String scopeKey);

    Optional<FarmerPromotionSettingEntity> findByScopeKeyAndPromoKey(String scopeKey, String promoKey);
}
