package com.example.spring.model.dto.marketing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssortmentItemDto {
    private Long id;
    private Long organizationId;
    private Long productId;
    private String shopName;
    private String farmerDescription;
    private String region;
    private String name;
    private String category;
    private String productDescription;
    private String urlProduct;
    private String urlFarmer;
    private BigDecimal price;
    private Integer stock;
    private Integer monthlySales;
    private List<String> tags;
}
