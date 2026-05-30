package com.example.spring.repositories.marketing;

import com.example.spring.model.entity.marketing.AssortmentItemEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AssortmentItemRepository extends CrudRepository<AssortmentItemEntity, Long> {

    List<AssortmentItemEntity> findAllByOrderByCategoryAscNameAsc();

    List<AssortmentItemEntity> findByFarmerIdOrderByCategoryAscNameAsc(Long farmerId);

    @Query("select coalesce(max(item.id), 0) from AssortmentItemEntity item")
    Long maxId();

    @Query("select coalesce(max(item.productId), 0) from AssortmentItemEntity item")
    Long maxProductId();
}
