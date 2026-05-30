package com.example.spring.controller;

import com.example.spring.model.dto.marketing.AdminEventDto;
import com.example.spring.model.dto.marketing.AiHealthResponse;
import com.example.spring.model.dto.marketing.AiTextRequest;
import com.example.spring.model.dto.marketing.AiTextResponse;
import com.example.spring.model.dto.marketing.AssortmentItemDto;
import com.example.spring.model.dto.marketing.CalendarPeriod;
import com.example.spring.model.dto.marketing.DashboardDto;
import com.example.spring.model.dto.marketing.FarmerCalendarStateDto;
import com.example.spring.model.dto.marketing.FarmerDto;
import com.example.spring.model.dto.marketing.MarketingCalendarResponse;
import com.example.spring.service.GigaChatService;
import com.example.spring.service.jpa.MarketingCalendarJpaComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/farmer")
@RequiredArgsConstructor
public class MarketingCalendarController {

    private final MarketingCalendarJpaComponent jpaComponent;
    private final GigaChatService gigaChatService;

    @GetMapping("/calendar")
    public MarketingCalendarResponse getCalendar(
            @RequestParam(defaultValue = "month") String period,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long organizationId
    ) {
        LocalDate now = LocalDate.now();
        int targetYear = year == null ? now.getYear() : year;
        int targetMonth = month == null ? now.getMonthValue() : month;
        return jpaComponent.buildCalendar(targetYear, targetMonth, calendarPeriod(period), category, organizationId);
    }

    private String calendarPeriod(String period) {
        try {
            return CalendarPeriod.normalize(period);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    @GetMapping("/assortment")
    public List<AssortmentItemDto> getAssortment(@RequestParam(required = false) Long organizationId) {
        return jpaComponent.getAssortment(organizationId);
    }

    @PostMapping("/assortment")
    public AssortmentItemDto createAssortmentItem(@RequestBody AssortmentItemDto request) {
        return jpaComponent.createAssortmentItem(request);
    }

    @GetMapping("/farmers")
    public List<FarmerDto> getFarmers() {
        return jpaComponent.getFarmers();
    }

    @GetMapping("/dashboard")
    public DashboardDto getDashboard() {
        return jpaComponent.getDashboard();
    }

    @GetMapping("/state")
    public FarmerCalendarStateDto getCalendarState(@RequestParam(required = false) Long organizationId) {
        return jpaComponent.getCalendarState(organizationId);
    }

    @PutMapping("/state/actions")
    public FarmerCalendarStateDto saveActionState(@RequestBody FarmerCalendarStateDto.ActionStateRequest request) {
        return jpaComponent.saveActionState(request);
    }

    @PutMapping("/state/promotion")
    public FarmerCalendarStateDto savePromotionSettings(@RequestBody FarmerCalendarStateDto.PromotionSettingsRequest request) {
        return jpaComponent.savePromotionSettings(request);
    }

    @PutMapping("/state/actuals")
    public FarmerCalendarStateDto saveCampaignActuals(@RequestBody FarmerCalendarStateDto.ActualRequest request) {
        return jpaComponent.saveCampaignActuals(request);
    }

    @GetMapping("/admin/events")
    public List<AdminEventDto> getAdminEvents() {
        return jpaComponent.getAdminEvents();
    }

    @PostMapping("/admin/events")
    public AdminEventDto createAdminEvent(@RequestBody AdminEventDto request) {
        return jpaComponent.saveAdminEvent(null, request);
    }

    @PutMapping("/admin/events/{eventId}")
    public AdminEventDto updateAdminEvent(@PathVariable Long eventId, @RequestBody AdminEventDto request) {
        return jpaComponent.saveAdminEvent(eventId, request);
    }

    @DeleteMapping("/admin/events/{eventId}")
    public void deleteAdminEvent(@PathVariable Long eventId) {
        jpaComponent.deleteAdminEvent(eventId);
    }

    @PostMapping("/ai/social-post")
    public AiTextResponse generateSocialPost(@RequestBody AiTextRequest request) {
        return AiTextResponse.builder()
                .text(gigaChatService.complete(request.getPrompt()))
                .build();
    }

    @PostMapping("/ai/metrics-analysis")
    public AiTextResponse analyzeMetrics(@RequestBody AiTextRequest request) {
        return AiTextResponse.builder()
                .text(gigaChatService.complete(request.getPrompt()))
                .build();
    }

    @GetMapping("/ai/health")
    public AiHealthResponse aiHealth() {
        try {
            return AiHealthResponse.builder()
                    .ok(true)
                    .message("GigaChat is available")
                    .sample(gigaChatService.healthCheck())
                    .build();
        } catch (ResponseStatusException exception) {
            return AiHealthResponse.builder()
                    .ok(false)
                    .message(exception.getReason())
                    .sample("")
                    .build();
        } catch (RuntimeException exception) {
            return AiHealthResponse.builder()
                    .ok(false)
                    .message(exception.getMessage())
                    .sample("")
                    .build();
        }
    }
}
