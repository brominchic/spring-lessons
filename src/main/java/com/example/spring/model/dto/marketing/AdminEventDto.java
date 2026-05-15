package com.example.spring.model.dto.marketing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminEventDto {
    private Long id;
    private Integer eventMonth;
    private Integer eventDay;
    private LocalDate startDate;
    private LocalDate endDate;
    private String title;
    private String eventType;
    private Integer roiPercent;
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    @Builder.Default
    private List<String> channels = new ArrayList<>();
    @Builder.Default
    private List<BoostDto> boosts = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BoostDto {
        private Long id;
        private String tag;
        private BigDecimal multiplier;
        private String rationale;
        private String sourceTitle;
        private String sourceUrl;
    }
}
