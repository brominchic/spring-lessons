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

import java.time.LocalDateTime;

@Table(name = "farmer_promotion_settings")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FarmerPromotionSettingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scope_key", nullable = false, length = 80)
    private String scopeKey;

    @Column(name = "promo_key", nullable = false, length = 600)
    private String promoKey;

    @Column(name = "discount_key", nullable = false, length = 600)
    private String discountKey;

    @Column(name = "promo_code", nullable = false, length = 24)
    private String promoCode;

    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
