package com.example.spring.model.dto.marketing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarketingCalendarResponse {
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private List<String> categories;
    private List<CalendarEventDto> events;
    private SummaryDto summary;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CalendarEventDto {
        private Long id;
        private LocalDate date;
        private LocalDate endDate;
        private String title;
        private String eventType;
        private String category;
        private Long organizationId;
        private Long productId;
        private String shopName;
        private String offer;
        private String product;
        private List<String> channels;
        private String socialPost;
        private String marketplaceAction;
        private BigDecimal productPrice;
        private Integer stock;
        private Integer baselineOrders;
        private Integer seasonalOrders;
        private Integer expectedOrders;
        private Integer upliftPercent;
        private Integer roiPercent;
        private Integer discountPercent;
        private Integer marginPercent;
        private Boolean stockLimited;
        private BigDecimal grossRevenue;
        private BigDecimal incrementalRevenue;
        private BigDecimal grossProfit;
        private BigDecimal marketingCost;
        private BigDecimal discountCost;
        private BigDecimal channelCost;
        private BigDecimal operationalCost;
        private String evidence;
        private String rationale;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SummaryDto {
        private Integer totalEvents;
        private Integer expectedOrders;
        private Integer averageRoiPercent;
        private String bestBet;
        private List<String> recommendations;
    }
}
