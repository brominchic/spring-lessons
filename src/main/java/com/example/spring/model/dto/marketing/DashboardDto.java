package com.example.spring.model.dto.marketing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardDto {
    private String farmerName;
    private String region;
    private Integer activeSku;
    private Integer monthlyOrders;
    private Integer repeatRate;
    private String priority;
    private List<MetricDto> metrics;
    private List<String> mockTasks;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MetricDto {
        private String label;
        private String value;
        private String hint;
    }
}
