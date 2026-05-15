package com.example.spring.model.entity.marketing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Table(name = "event_category_boosts")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventCategoryBoostEntity {

    @Id
    private Long id;

    @Column(name = "event_template_id", nullable = false)
    private Long eventTemplateId;

    @Column(nullable = false)
    private String tag;

    @Column(nullable = false)
    private BigDecimal multiplier;

    @Column(nullable = false)
    private String rationale;

    @Column(name = "source_title", nullable = false)
    private String sourceTitle;

    @Column(name = "source_url", nullable = false)
    private String sourceUrl;
}
