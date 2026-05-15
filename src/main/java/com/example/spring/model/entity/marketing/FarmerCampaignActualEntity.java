package com.example.spring.model.entity.marketing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "farmer_campaign_actuals")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FarmerCampaignActualEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scope_key", nullable = false, length = 80)
    private String scopeKey;

    @Column(name = "actual_key", nullable = false, length = 600)
    private String actualKey;

    @Column(name = "actual_orders", nullable = false)
    private Integer actualOrders;

    @Column(name = "actual_revenue", nullable = false)
    private BigDecimal actualRevenue;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
