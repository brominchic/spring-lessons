package com.example.spring.model.entity.marketing;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Table(name = "assortment_items")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssortmentItemEntity {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private FarmerEntity farmer;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(name = "product_description", nullable = false)
    private String productDescription;

    @Column(name = "url_product", nullable = false)
    private String urlProduct;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "monthly_sales", nullable = false)
    private Integer monthlySales;

    @ElementCollection
    @CollectionTable(name = "assortment_item_tags", joinColumns = @JoinColumn(name = "assortment_item_id"))
    @Column(name = "tag", nullable = false)
    @Builder.Default
    private List<String> tags = new ArrayList<>();
}
