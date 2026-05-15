package com.example.spring.model.dto.marketing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FarmerCalendarStateDto {

    @Builder.Default
    private Map<String, Boolean> appliedActions = new LinkedHashMap<>();

    @Builder.Default
    private Map<String, String> promoCodes = new LinkedHashMap<>();

    @Builder.Default
    private Map<String, Integer> discountPercents = new LinkedHashMap<>();

    @Builder.Default
    private Map<String, ActualDto> actuals = new LinkedHashMap<>();

    @Builder.Default
    private List<HistoryDto> history = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActionStateRequest {
        private Long organizationId;
        private String actionKey;
        private Boolean applied;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PromotionSettingsRequest {
        private Long organizationId;
        private String promoKey;
        private String discountKey;
        private String promoCode;
        private Integer discountPercent;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ActualDto {
        private Integer actualOrders;
        private BigDecimal actualRevenue;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActualRequest {
        private Long organizationId;
        private String actualKey;
        private Integer actualOrders;
        private BigDecimal actualRevenue;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class HistoryDto {
        private String actionType;
        private String itemKey;
        private String details;
        private LocalDateTime createdAt;
    }
}
