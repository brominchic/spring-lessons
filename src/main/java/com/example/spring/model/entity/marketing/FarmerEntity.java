package com.example.spring.model.entity.marketing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "farmers")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FarmerEntity {

    @Id
    private Long id;

    @Column(name = "shop_name", nullable = false)
    private String shopName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String region;

    @Column(name = "url_farmer", nullable = false)
    private String urlFarmer;
}
