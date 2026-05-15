package com.example.spring.repositories.marketing;

import com.example.spring.model.entity.marketing.EventCategoryBoostEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventCategoryBoostRepository extends CrudRepository<EventCategoryBoostEntity, Long> {

    List<EventCategoryBoostEntity> findAllByOrderByEventTemplateIdAscMultiplierDesc();
}
