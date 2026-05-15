package com.example.spring.repositories.marketing;

import com.example.spring.model.entity.marketing.EventTemplateEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventTemplateRepository extends CrudRepository<EventTemplateEntity, Long> {

    List<EventTemplateEntity> findAllByOrderByEventMonthAscEventDayAsc();
}
