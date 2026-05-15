package com.example.spring.model.entity.marketing;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table(name = "event_templates")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventTemplateEntity {

    @Id
    private Long id;

    @Column(name = "event_month", nullable = false)
    private Integer eventMonth;

    @Column(name = "event_day", nullable = false)
    private Integer eventDay;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false)
    private String title;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "roi_percent", nullable = false)
    private Integer roiPercent;

    @ElementCollection
    @CollectionTable(name = "event_template_tags", joinColumns = @JoinColumn(name = "event_template_id"))
    @Column(name = "tag", nullable = false)
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "event_template_channels", joinColumns = @JoinColumn(name = "event_template_id"))
    @Column(name = "channel", nullable = false)
    @Builder.Default
    private List<String> channels = new ArrayList<>();
}
