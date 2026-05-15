package com.example.spring.service.jpa;

import com.example.spring.model.dto.marketing.AdminEventDto;
import com.example.spring.model.dto.marketing.AssortmentItemDto;
import com.example.spring.model.dto.marketing.CalendarPeriod;
import com.example.spring.model.dto.marketing.DashboardDto;
import com.example.spring.model.dto.marketing.FarmerCalendarStateDto;
import com.example.spring.model.dto.marketing.FarmerDto;
import com.example.spring.model.dto.marketing.MarketingCalendarResponse;
import com.example.spring.model.entity.marketing.AssortmentItemEntity;
import com.example.spring.model.entity.marketing.EventCategoryBoostEntity;
import com.example.spring.model.entity.marketing.EventTemplateEntity;
import com.example.spring.model.entity.marketing.FarmerActionStateEntity;
import com.example.spring.model.entity.marketing.FarmerCalendarHistoryEntity;
import com.example.spring.model.entity.marketing.FarmerCampaignActualEntity;
import com.example.spring.model.entity.marketing.FarmerPromotionSettingEntity;
import com.example.spring.repositories.marketing.AssortmentItemRepository;
import com.example.spring.repositories.marketing.EventCategoryBoostRepository;
import com.example.spring.repositories.marketing.EventTemplateRepository;
import com.example.spring.repositories.marketing.FarmerActionStateRepository;
import com.example.spring.repositories.marketing.FarmerCalendarHistoryRepository;
import com.example.spring.repositories.marketing.FarmerCampaignActualRepository;
import com.example.spring.repositories.marketing.FarmerPromotionSettingRepository;
import com.example.spring.repositories.marketing.FarmerRepository;
import com.example.spring.service.component.mapper.AssortmentItemMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MarketingCalendarJpaComponent {

    private final AssortmentItemMapper mapper;
    private final AssortmentItemRepository assortmentRepository;
    private final FarmerRepository farmerRepository;
    private final EventTemplateRepository eventTemplateRepository;
    private final EventCategoryBoostRepository eventCategoryBoostRepository;
    private final FarmerActionStateRepository farmerActionStateRepository;
    private final FarmerPromotionSettingRepository farmerPromotionSettingRepository;
    private final FarmerCampaignActualRepository farmerCampaignActualRepository;
    private final FarmerCalendarHistoryRepository farmerCalendarHistoryRepository;

    @Transactional(readOnly = true)
    public MarketingCalendarResponse buildCalendar(int year, int month, String period, String category, Long organizationId) {
        YearMonth startMonth = YearMonth.of(year, month);
        LocalDate start = startMonth.atDay(1);
        LocalDate end;
        if (CalendarPeriod.isQuarter(period)) {
            end = start.plusMonths(3).minusDays(1);
        } else {
            end = startMonth.atEndOfMonth();
        }

        List<EventTemplate> templates = eventTemplates(start, end);
        List<AssortmentItemDto> assortment = getAssortment(organizationId);
        List<MarketingCalendarResponse.CalendarEventDto> events = new ArrayList<>();
        Set<Long> usedAssortmentItemIds = new HashSet<>();
        long id = 1L;

        for (EventTemplate template : templates) {
            EventCandidate candidate = template.bestCandidate(assortment, category, usedAssortmentItemIds);
            if (candidate == null) {
                continue;
            }

            AssortmentItemDto item = candidate.getItem();
            usedAssortmentItemIds.add(item.getId());
            BoostRule boost = candidate.getBoost();
            PromotionPlan promotion = promotionPlan(item, template, boost);
            ForecastEconomics economics = forecastEconomics(item, template, boost, promotion);
            events.add(MarketingCalendarResponse.CalendarEventDto.builder().
                    id(id++).
                    date(template.getDate()).
                    endDate(template.getEndDate()).
                    title(template.getTitle()).
                    eventType(template.getType()).
                    category(item.getCategory()).
                    organizationId(item.getOrganizationId()).
                    productId(item.getProductId()).
                    shopName(item.getShopName()).
                    offer(template.offer(item, promotion)).
                    product(item.getName()).
                    channels(template.getChannels()).
                    socialPost(template.socialPost(item, promotion)).
                    marketplaceAction(template.marketplaceAction(item, promotion)).
                    productPrice(money(item.getPrice())).
                    stock(safeStock(item)).
                    baselineOrders(economics.getBaselineOrders()).
                    seasonalOrders(economics.getSeasonalOrders()).
                    expectedOrders(economics.getExpectedOrders()).
                    upliftPercent(economics.getUpliftPercent()).
                    roiPercent(economics.getRoiPercent()).
                    discountPercent(promotion.getDiscountPercent()).
                    marginPercent(economics.getMarginPercent()).
                    stockLimited(economics.isStockLimited()).
                    grossRevenue(economics.getGrossRevenue()).
                    incrementalRevenue(economics.getIncrementalRevenue()).
                    grossProfit(economics.getGrossProfit()).
                    marketingCost(economics.getMarketingCost()).
                    discountCost(economics.getDiscountCost()).
                    channelCost(economics.getChannelCost()).
                    operationalCost(economics.getOperationalCost()).
                    evidence(boost.getSourceTitle() + ": " + boost.getRationale()).
                    rationale(template.rationale(item, boost)).
                    build());
        }

        events.sort(Comparator.comparing(MarketingCalendarResponse.CalendarEventDto::getDate));
        int totalOrders = 0;
        int roiSum = 0;
        for (MarketingCalendarResponse.CalendarEventDto event : events) {
            totalOrders += event.getExpectedOrders();
            roiSum += event.getRoiPercent();
        }

        int avgRoi = events.isEmpty() ? 0 : (int) Math.round((double) roiSum / events.size());
        String bestBet = "Нет подходящих событий для выбранного фильтра";
        if (!events.isEmpty()) {
            MarketingCalendarResponse.CalendarEventDto bestEvent = events.get(0);
            for (MarketingCalendarResponse.CalendarEventDto event : events) {
                if (event.getExpectedOrders() > bestEvent.getExpectedOrders()) {
                    bestEvent = event;
                }
            }
            bestBet = bestEvent.getProduct() + ": " + bestEvent.getTitle();
        }

        return MarketingCalendarResponse.builder().
                periodStart(start).
                periodEnd(end).
                categories(categories(assortment)).
                events(events).
                summary(MarketingCalendarResponse.SummaryDto.builder().
                        totalEvents(events.size()).
                        expectedOrders(totalOrders).
                        averageRoiPercent(avgRoi).
                        bestBet(bestBet).
                        recommendations(List.of(
                                "События ранжируются по совпадению тегов товара, сезонных коэффициентов и таблицы сезонных продуктов для меню русской кухни.",
                                "ROI считается по экономике конкретного SKU: цена, остаток, скидка, маржинальность и операционные затраты.",
                                "Запускайте акцию за 5-7 дней до события и усиливайте ее в день события.",
                                "Для крупных пиков вроде Нового года готовьте витрину и предзаказы за 10-14 дней.",
                                "Для подарочных товаров добавляйте готовые наборы и промокод на повторную покупку."
                        )).
                        build()).
                build();
    }

    @Transactional(readOnly = true)
    public List<AssortmentItemDto> getAssortment(Long organizationId) {
        List<AssortmentItemEntity> items;
        if (organizationId == null) {
            items = assortmentRepository.findAllByOrderByCategoryAscNameAsc();
        } else {
            items = assortmentRepository.findByFarmerIdOrderByCategoryAscNameAsc(organizationId);
        }

        List<AssortmentItemDto> result = new ArrayList<>();
        for (AssortmentItemEntity item : items) {
            result.add(mapper.entityToDto(item));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<FarmerDto> getFarmers() {
        return farmerRepository.findAllDto();
    }

    @Transactional(readOnly = true)
    public FarmerCalendarStateDto getCalendarState(Long organizationId) {
        String scopeKey = calendarStateScopeKey(organizationId);
        Map<String, Boolean> appliedActions = new LinkedHashMap<>();
        for (FarmerActionStateEntity state : farmerActionStateRepository.findByScopeKeyOrderByUpdatedAtDesc(scopeKey)) {
            if (Boolean.TRUE.equals(state.getApplied())) {
                appliedActions.put(state.getActionKey(), true);
            }
        }

        Map<String, String> promoCodes = new LinkedHashMap<>();
        Map<String, Integer> discountPercents = new LinkedHashMap<>();
        for (FarmerPromotionSettingEntity setting : farmerPromotionSettingRepository.findByScopeKeyOrderByUpdatedAtDesc(scopeKey)) {
            promoCodes.put(setting.getPromoKey(), setting.getPromoCode());
            discountPercents.put(setting.getDiscountKey(), setting.getDiscountPercent());
        }

        Map<String, FarmerCalendarStateDto.ActualDto> actuals = new LinkedHashMap<>();
        for (FarmerCampaignActualEntity actual : farmerCampaignActualRepository.findByScopeKeyOrderByUpdatedAtDesc(scopeKey)) {
            actuals.put(actual.getActualKey(), FarmerCalendarStateDto.ActualDto.builder()
                    .actualOrders(actual.getActualOrders())
                    .actualRevenue(actual.getActualRevenue())
                    .build());
        }

        List<FarmerCalendarStateDto.HistoryDto> history = new ArrayList<>();
        for (FarmerCalendarHistoryEntity item : farmerCalendarHistoryRepository.findTop20ByScopeKeyOrderByCreatedAtDesc(scopeKey)) {
            history.add(FarmerCalendarStateDto.HistoryDto.builder()
                    .actionType(item.getActionType())
                    .itemKey(item.getItemKey())
                    .details(item.getDetails())
                    .createdAt(item.getCreatedAt())
                    .build());
        }

        return FarmerCalendarStateDto.builder()
                .appliedActions(appliedActions)
                .promoCodes(promoCodes)
                .discountPercents(discountPercents)
                .actuals(actuals)
                .history(history)
                .build();
    }

    @Transactional
    public FarmerCalendarStateDto saveActionState(FarmerCalendarStateDto.ActionStateRequest request) {
        if (request == null || isBlank(request.getActionKey())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Action key is required");
        }

        String scopeKey = calendarStateScopeKey(request.getOrganizationId());
        if (!Boolean.TRUE.equals(request.getApplied())) {
            farmerActionStateRepository.findByScopeKeyAndActionKey(scopeKey, request.getActionKey().trim())
                    .ifPresent((state) -> farmerActionStateRepository.deleteById(state.getId()));
            recordHistory(scopeKey, "action-canceled", request.getActionKey(), "Действие отменено");
            return getCalendarState(request.getOrganizationId());
        }

        LocalDateTime now = LocalDateTime.now();
        FarmerActionStateEntity state = farmerActionStateRepository
                .findByScopeKeyAndActionKey(scopeKey, request.getActionKey().trim())
                .orElseGet(() -> FarmerActionStateEntity.builder()
                        .scopeKey(scopeKey)
                        .actionKey(request.getActionKey().trim())
                        .createdAt(now)
                        .build());
        state.setApplied(true);
        state.setUpdatedAt(now);
        if (state.getCreatedAt() == null) {
            state.setCreatedAt(now);
        }
        farmerActionStateRepository.save(state);
        recordHistory(scopeKey, "action-applied", request.getActionKey(), "Действие применено");
        return getCalendarState(request.getOrganizationId());
    }

    @Transactional
    public FarmerCalendarStateDto savePromotionSettings(FarmerCalendarStateDto.PromotionSettingsRequest request) {
        validatePromotionSettings(request);
        String scopeKey = calendarStateScopeKey(request.getOrganizationId());
        String promoCode = normalizePromoCode(request.getPromoCode());
        LocalDateTime now = LocalDateTime.now();

        FarmerPromotionSettingEntity setting = farmerPromotionSettingRepository
                .findByScopeKeyAndPromoKey(scopeKey, request.getPromoKey().trim())
                .orElseGet(() -> FarmerPromotionSettingEntity.builder()
                        .scopeKey(scopeKey)
                        .promoKey(request.getPromoKey().trim())
                        .createdAt(now)
                        .build());
        setting.setDiscountKey(request.getDiscountKey().trim());
        setting.setPromoCode(promoCode);
        setting.setDiscountPercent(request.getDiscountPercent());
        setting.setUpdatedAt(now);
        if (setting.getCreatedAt() == null) {
            setting.setCreatedAt(now);
        }
        farmerPromotionSettingRepository.save(setting);
        recordHistory(
                scopeKey,
                "promotion-updated",
                request.getPromoKey(),
                "Промокод " + promoCode + ", скидка " + request.getDiscountPercent() + "%"
        );
        return getCalendarState(request.getOrganizationId());
    }

    @Transactional
    public FarmerCalendarStateDto saveCampaignActuals(FarmerCalendarStateDto.ActualRequest request) {
        validateCampaignActuals(request);
        String scopeKey = calendarStateScopeKey(request.getOrganizationId());
        LocalDateTime now = LocalDateTime.now();
        FarmerCampaignActualEntity actual = farmerCampaignActualRepository
                .findByScopeKeyAndActualKey(scopeKey, request.getActualKey().trim())
                .orElseGet(() -> FarmerCampaignActualEntity.builder()
                        .scopeKey(scopeKey)
                        .actualKey(request.getActualKey().trim())
                        .createdAt(now)
                        .build());
        actual.setActualOrders(request.getActualOrders());
        actual.setActualRevenue(money(request.getActualRevenue()));
        actual.setUpdatedAt(now);
        if (actual.getCreatedAt() == null) {
            actual.setCreatedAt(now);
        }
        farmerCampaignActualRepository.save(actual);
        recordHistory(
                scopeKey,
                "actual-updated",
                request.getActualKey(),
                "Факт: " + request.getActualOrders() + " заказов, " + money(request.getActualRevenue()) + " руб."
        );
        return getCalendarState(request.getOrganizationId());
    }

    @Transactional(readOnly = true)
    public List<AdminEventDto> getAdminEvents() {
        Map<Long, List<AdminEventDto.BoostDto>> boostsByEvent = new HashMap<>();
        for (EventCategoryBoostEntity boost : eventCategoryBoostRepository.findAllByOrderByEventTemplateIdAscMultiplierDesc()) {
            boostsByEvent.computeIfAbsent(boost.getEventTemplateId(), ignored -> new ArrayList<>())
                    .add(AdminEventDto.BoostDto.builder()
                            .id(boost.getId())
                            .tag(boost.getTag())
                            .multiplier(boost.getMultiplier())
                            .rationale(boost.getRationale())
                            .sourceTitle(boost.getSourceTitle())
                            .sourceUrl(boost.getSourceUrl())
                            .build());
        }

        List<AdminEventDto> result = new ArrayList<>();
        for (EventTemplateEntity template : eventTemplateRepository.findAllByOrderByEventMonthAscEventDayAsc()) {
            result.add(AdminEventDto.builder()
                    .id(template.getId())
                    .eventMonth(template.getEventMonth())
                    .eventDay(template.getEventDay())
                    .startDate(template.getStartDate())
                    .endDate(template.getEndDate())
                    .title(template.getTitle())
                    .eventType(template.getEventType())
                    .roiPercent(template.getRoiPercent())
                    .tags(new ArrayList<>(template.getTags()))
                    .channels(new ArrayList<>(template.getChannels()))
                    .boosts(boostsByEvent.getOrDefault(template.getId(), List.of()))
                    .build());
        }
        return result;
    }

    @Transactional
    public AdminEventDto saveAdminEvent(Long eventId, AdminEventDto request) {
        validateAdminEvent(request);
        Long targetId = eventId;
        if (targetId == null) {
            targetId = request.getId();
        }
        if (targetId == null) {
            targetId = nextEventTemplateId();
        }
        LocalDate startDate = eventStartDate(request);
        LocalDate endDate = eventEndDate(request, startDate);

        EventTemplateEntity template = EventTemplateEntity.builder()
                .id(targetId)
                .eventMonth(startDate.getMonthValue())
                .eventDay(startDate.getDayOfMonth())
                .title(request.getTitle().trim())
                .eventType(request.getEventType().trim())
                .roiPercent(request.getRoiPercent())
                .tags(cleanList(request.getTags()))
                .channels(cleanList(request.getChannels()))
                .build();
        template.setStartDate(startDate);
        template.setEndDate(endDate);
        eventTemplateRepository.save(template);

        long nextBoostId = nextEventCategoryBoostId();
        deleteBoostsByEventTemplateId(targetId);
        if (request.getBoosts() != null) {
            for (AdminEventDto.BoostDto boost : request.getBoosts()) {
                if (boost == null || isBlank(boost.getTag())) {
                    continue;
                }
                eventCategoryBoostRepository.save(EventCategoryBoostEntity.builder()
                        .id(nextBoostId++)
                        .eventTemplateId(targetId)
                        .tag(boost.getTag().trim())
                        .multiplier(boost.getMultiplier() == null ? BigDecimal.ONE : boost.getMultiplier())
                        .rationale(defaultText(boost.getRationale(), "Админское правило без дополнительного описания."))
                        .sourceTitle(defaultText(boost.getSourceTitle(), "Админская гипотеза"))
                        .sourceUrl(defaultText(boost.getSourceUrl(), ""))
                        .build());
            }
        }

        return getAdminEvent(targetId);
    }

    @Transactional
    public void deleteAdminEvent(Long eventId) {
        deleteBoostsByEventTemplateId(eventId);
        eventTemplateRepository.deleteById(eventId);
    }

    @Transactional(readOnly = true)
    public AdminEventDto getAdminEvent(Long eventId) {
        EventTemplateEntity template = eventTemplateRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        List<AdminEventDto.BoostDto> boosts = new ArrayList<>();
        for (EventCategoryBoostEntity boost : boostsByEventTemplateId(eventId)) {
            boosts.add(AdminEventDto.BoostDto.builder()
                    .id(boost.getId())
                    .tag(boost.getTag())
                    .multiplier(boost.getMultiplier())
                    .rationale(boost.getRationale())
                    .sourceTitle(boost.getSourceTitle())
                    .sourceUrl(boost.getSourceUrl())
                    .build());
        }
        return AdminEventDto.builder()
                .id(template.getId())
                .eventMonth(template.getEventMonth())
                .eventDay(template.getEventDay())
                .startDate(template.getStartDate())
                .endDate(template.getEndDate())
                .title(template.getTitle())
                .eventType(template.getEventType())
                .roiPercent(template.getRoiPercent())
                .tags(new ArrayList<>(template.getTags()))
                .channels(new ArrayList<>(template.getChannels()))
                .boosts(boosts)
                .build();
    }

    @Transactional(readOnly = true)
    public DashboardDto getDashboard() {
        int activeSku = (int) assortmentRepository.count();
        long farmers = farmerRepository.count();
        List<DashboardDto.MetricDto> metrics = new ArrayList<>();
        metrics.add(new DashboardDto.MetricDto("SKU в базе", String.valueOf(activeSku), "реальные товары из файла"));
        metrics.add(new DashboardDto.MetricDto("Фермеры", String.valueOf(farmers), "уникальные продавцы"));
        metrics.add(new DashboardDto.MetricDto("Средний ROI", "142%", "по планируемым акциям"));
        metrics.add(new DashboardDto.MetricDto("Повторные заказы", "24%", "мок для демо-кабинета"));

        return DashboardDto.builder().
                farmerName("65 фермеров").
                region("Москва").
                activeSku(activeSku).
                monthlyOrders(327).
                repeatRate(24).
                priority("Календарь строится по реальному ассортименту из farmers_sku.xlsx").
                metrics(metrics).
                mockTasks(List.of(
                        "Обновить фото для трех SKU",
                        "Собрать набор ко Дню семьи",
                        "Подготовить рецепт с сыром и иван-чаем",
                        "Отправить тему статьи в блог Своё Родное"
                )).
                build();
    }

    private void validateAdminEvent(AdminEventDto request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event payload is required");
        }
        LocalDate startDate = eventStartDate(request);
        LocalDate endDate = eventEndDate(request, startDate);
        if (endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event end date must be after start date");
        }
        if (isBlank(request.getTitle())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event title is required");
        }
        if (isBlank(request.getEventType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event type is required");
        }
        if (request.getRoiPercent() == null || request.getRoiPercent() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ROI must be zero or positive");
        }
    }

    private void validatePromotionSettings(FarmerCalendarStateDto.PromotionSettingsRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Promotion payload is required");
        }
        if (isBlank(request.getPromoKey())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Promo key is required");
        }
        if (isBlank(request.getDiscountKey())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Discount key is required");
        }
        if (isBlank(request.getPromoCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Promo code is required");
        }
        if (request.getDiscountPercent() == null || request.getDiscountPercent() < 0 || request.getDiscountPercent() > 90) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Discount must be between 0 and 90");
        }
    }

    private void validateCampaignActuals(FarmerCalendarStateDto.ActualRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Actual payload is required");
        }
        if (isBlank(request.getActualKey())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Actual key is required");
        }
        if (request.getActualOrders() == null || request.getActualOrders() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Actual orders must be zero or positive");
        }
        if (request.getActualRevenue() == null || request.getActualRevenue().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Actual revenue must be zero or positive");
        }
    }

    private void recordHistory(String scopeKey, String actionType, String itemKey, String details) {
        farmerCalendarHistoryRepository.save(FarmerCalendarHistoryEntity.builder()
                .scopeKey(scopeKey)
                .actionType(actionType)
                .itemKey(itemKey.trim())
                .details(details.length() > 1000 ? details.substring(0, 1000) : details)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private String calendarStateScopeKey(Long organizationId) {
        if (organizationId == null) {
            return "all-farmers";
        }
        if (!farmerRepository.existsById(organizationId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found");
        }
        return "farmer:" + organizationId;
    }

    private String normalizePromoCode(String value) {
        String promoCode = value == null
                ? ""
                : value.trim()
                .replaceAll("\\s+", "-")
                .replaceAll("[^A-Za-zА-Яа-я0-9_-]", "")
                .toUpperCase(Locale.ROOT);
        if (promoCode.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Promo code is required");
        }
        return promoCode.length() > 24 ? promoCode.substring(0, 24) : promoCode;
    }

    private List<String> cleanList(List<String> values) {
        if (values == null) {
            return new ArrayList<>();
        }
        Set<String> result = new LinkedHashSet<>();
        for (String value : values) {
            if (!isBlank(value)) {
                result.add(value.trim());
            }
        }
        return new ArrayList<>(result);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String defaultText(String value, String fallback) {
        return isBlank(value) ? fallback : value.trim();
    }

    private LocalDate eventStartDate(AdminEventDto request) {
        if (request.getStartDate() != null) {
            return request.getStartDate();
        }
        if (request.getEventMonth() == null || request.getEventDay() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event start date is required");
        }
        try {
            return LocalDate.of(LocalDate.now().getYear(), request.getEventMonth(), request.getEventDay());
        } catch (DateTimeException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid event month/day", exception);
        }
    }

    private LocalDate eventEndDate(AdminEventDto request, LocalDate startDate) {
        if (request.getEndDate() != null) {
            return request.getEndDate();
        }
        return startDate;
    }

    private List<EventCategoryBoostEntity> boostsByEventTemplateId(Long eventTemplateId) {
        List<EventCategoryBoostEntity> result = new ArrayList<>();
        for (EventCategoryBoostEntity boost : eventCategoryBoostRepository.findAll()) {
            if (eventTemplateId.equals(boost.getEventTemplateId())) {
                result.add(boost);
            }
        }
        result.sort(Comparator.comparing(EventCategoryBoostEntity::getMultiplier).reversed());
        return result;
    }

    private void deleteBoostsByEventTemplateId(Long eventTemplateId) {
        for (EventCategoryBoostEntity boost : boostsByEventTemplateId(eventTemplateId)) {
            eventCategoryBoostRepository.deleteById(boost.getId());
        }
    }

    private Long nextEventTemplateId() {
        long maxId = 0;
        for (EventTemplateEntity template : eventTemplateRepository.findAll()) {
            maxId = Math.max(maxId, template.getId());
        }
        return maxId + 1;
    }

    private Long nextEventCategoryBoostId() {
        long maxId = 0;
        for (EventCategoryBoostEntity boost : eventCategoryBoostRepository.findAll()) {
            maxId = Math.max(maxId, boost.getId());
        }
        return maxId + 1;
    }

    private int baselineOrders(AssortmentItemDto item, EventTemplate template) {
        return capByStock(baselineDemand(item, template), item);
    }

    private int baselineDemand(AssortmentItemDto item, EventTemplate template) {
        double multiplier;
        switch (template.getType()) {
            case "Праздник" -> multiplier = 0.55;
            case "Сезонность" -> multiplier = 0.38;
            case "Тренд" -> multiplier = 0.30;
            default -> multiplier = 0.22;
        }
        return Math.max(8, (int) Math.round(item.getMonthlySales() * multiplier));
    }

    private int seasonalOrders(AssortmentItemDto item, EventTemplate template, BoostRule boost) {
        return capByStock(seasonalDemand(item, template, boost), item);
    }

    private int seasonalDemand(AssortmentItemDto item, EventTemplate template, BoostRule boost) {
        double demand = baselineDemand(item, template)
                * boost.getMultiplier().doubleValue()
                * campaignQualityMultiplier(template);
        return Math.max(8, (int) Math.round(demand));
    }

    private int expectedOrders(AssortmentItemDto item, EventTemplate template, BoostRule boost, PromotionPlan promotion) {
        return capByStock(expectedDemand(item, template, boost, promotion), item);
    }

    private int expectedDemand(AssortmentItemDto item, EventTemplate template, BoostRule boost, PromotionPlan promotion) {
        double demand = baselineDemand(item, template)
                * boost.getMultiplier().doubleValue()
                * campaignQualityMultiplier(template)
                * discountDemandMultiplier(promotion.getDiscountPercent());
        return Math.max(8, (int) Math.round(demand));
    }

    private int upliftPercent(int baselineOrders, int expectedOrders) {
        if (baselineOrders <= 0) {
            return expectedOrders > 0 ? 100 : 0;
        }
        return (int) Math.round(((double) expectedOrders - baselineOrders) * 100 / baselineOrders);
    }

    private PromotionPlan promotionPlan(AssortmentItemDto item, EventTemplate template, BoostRule boost) {
        if (safeStock(item) <= 0) {
            return new PromotionPlan(0, "EVENT0");
        }

        List<Integer> discountOptions = stockIsTight(item, template, boost)
                ? List.of(0, 5, 10)
                : List.of(5, 10, 15, 20, 25, 30);
        PromotionPlan bestPlan = null;
        ForecastEconomics bestEconomics = null;
        for (Integer discount : discountOptions) {
            PromotionPlan plan = new PromotionPlan(discount, "EVENT" + discount);
            ForecastEconomics economics = forecastEconomics(item, template, boost, plan);
            if (bestPlan == null || economics.compareTo(bestEconomics) > 0) {
                bestPlan = plan;
                bestEconomics = economics;
            }
        }

        return bestPlan;
    }

    private ForecastEconomics forecastEconomics(
            AssortmentItemDto item,
            EventTemplate template,
            BoostRule boost,
            PromotionPlan promotion
    ) {
        int baselineOrders = baselineOrders(item, template);
        int seasonalOrders = seasonalOrders(item, template, boost);
        int expectedOrders = expectedOrders(item, template, boost, promotion);
        int upliftPercent = upliftPercent(baselineOrders, expectedOrders);
        BigDecimal price = money(item.getPrice());
        BigDecimal discountRate = percentRate(promotion.getDiscountPercent());
        BigDecimal marginRate = marginRate(item);
        int marginPercent = marginRate.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).intValue();

        BigDecimal fullRevenue = money(price.multiply(BigDecimal.valueOf(expectedOrders)));
        BigDecimal grossRevenue = money(fullRevenue.multiply(BigDecimal.ONE.subtract(discountRate)));
        BigDecimal baselineRevenue = money(price.multiply(BigDecimal.valueOf(baselineOrders)));
        BigDecimal incrementalRevenue = money(grossRevenue.subtract(baselineRevenue));
        BigDecimal grossProfit = money(grossRevenue.multiply(marginRate));
        BigDecimal baselineGrossProfit = money(baselineRevenue.multiply(marginRate));
        BigDecimal incrementalGrossProfit = grossProfit.subtract(baselineGrossProfit);
        BigDecimal discountCost = money(fullRevenue.multiply(discountRate));
        BigDecimal channelCost = BigDecimal.ZERO.setScale(0, RoundingMode.HALF_UP);
        BigDecimal operationalCost = money(grossRevenue.multiply(BigDecimal.valueOf(0.03)));
        BigDecimal marketingCost = money(discountCost.add(channelCost).add(operationalCost));
        BigDecimal netIncrementalProfit = incrementalGrossProfit.subtract(channelCost).subtract(operationalCost);
        int roiPercent = marketingCost.compareTo(BigDecimal.ZERO) == 0
                ? 0
                : netIncrementalProfit
                .multiply(BigDecimal.valueOf(100))
                .divide(marketingCost, 0, RoundingMode.HALF_UP)
                .intValue();

        return new ForecastEconomics(
                baselineOrders,
                seasonalOrders,
                expectedOrders,
                upliftPercent,
                roiPercent,
                marginPercent,
                safeStock(item) < Math.max(seasonalDemand(item, template, boost), expectedDemand(item, template, boost, promotion)),
                grossRevenue,
                incrementalRevenue,
                grossProfit,
                marketingCost,
                discountCost,
                channelCost,
                operationalCost
        );
    }

    private boolean stockIsTight(AssortmentItemDto item, EventTemplate template, BoostRule boost) {
        int stock = safeStock(item);
        return stock <= seasonalOrders(item, template, boost) || stock <= baselineOrders(item, template);
    }

    private int capByStock(int orders, AssortmentItemDto item) {
        return Math.min(Math.max(0, orders), safeStock(item));
    }

    private int safeStock(AssortmentItemDto item) {
        return item.getStock() == null ? 0 : Math.max(0, item.getStock());
    }

    private BigDecimal money(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(0, RoundingMode.HALF_UP);
        }
        return value.setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal percentRate(Integer percent) {
        return BigDecimal.valueOf(percent == null ? 0 : percent)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }

    private double campaignQualityMultiplier(EventTemplate template) {
        double value = template.getRoiPercent() == null ? 1.0 : template.getRoiPercent() / 140.0;
        return Math.max(0.75, Math.min(1.35, value));
    }

    private double discountDemandMultiplier(Integer discountPercent) {
        int discount = discountPercent == null ? 0 : discountPercent;
        if (discount <= 0) {
            return 1.0;
        }
        return 1.0 + Math.min(0.42, discount * 0.012);
    }

    private BigDecimal marginRate(AssortmentItemDto item) {
        String category = item.getCategory() == null ? "" : item.getCategory().toLowerCase(Locale.ROOT);
        if (category.contains("овощ") || category.contains("фрукт")) {
            return BigDecimal.valueOf(0.22);
        }
        if (category.contains("молоч") || category.contains("яйц")) {
            return BigDecimal.valueOf(0.25);
        }
        if (category.contains("рыб") || category.contains("морепродукт")) {
            return BigDecimal.valueOf(0.27);
        }
        if (category.contains("мяс")) {
            return BigDecimal.valueOf(0.28);
        }
        if (category.contains("бакале")) {
            return BigDecimal.valueOf(0.32);
        }
        if (category.contains("сыр")) {
            return BigDecimal.valueOf(0.33);
        }
        if (category.contains("мед") || category.contains("мёд")) {
            return BigDecimal.valueOf(0.35);
        }
        if (category.contains("слад")) {
            return BigDecimal.valueOf(0.38);
        }
        return BigDecimal.valueOf(0.30);
    }

    private List<String> categories(List<AssortmentItemDto> assortment) {
        Set<String> result = new LinkedHashSet<>();
        for (AssortmentItemDto item : assortment) {
            result.add(item.getCategory());
        }
        return new ArrayList<>(result);
    }

    private List<EventTemplate> eventTemplates(LocalDate start, LocalDate end) {
        int year = start.getYear();
        List<EventTemplate> result = new ArrayList<>();
        Map<Long, List<BoostRule>> boostRules = boostRulesByEvent();
        for (EventTemplateEntity template : eventTemplateRepository.findAllByOrderByEventMonthAscEventDayAsc()) {
            LocalDate eventStart = template.getStartDate() == null
                    ? LocalDate.of(year, template.getEventMonth(), template.getEventDay())
                    : template.getStartDate();
            LocalDate eventEnd = template.getEndDate() == null ? eventStart : template.getEndDate();
            EventTemplate eventTemplate = new EventTemplate(
                    template.getId(),
                    eventStart,
                    eventEnd,
                    template.getTitle(),
                    template.getEventType(),
                    new ArrayList<>(template.getTags()),
                    new ArrayList<>(template.getChannels()),
                    template.getRoiPercent(),
                    boostRules.getOrDefault(template.getId(), List.of(defaultBoost()))
            );
            if (eventTemplate.intersects(start, end)) {
                result.add(eventTemplate);
            }
        }
        return result;
    }

    private Map<Long, List<BoostRule>> boostRulesByEvent() {
        Map<Long, List<BoostRule>> result = new HashMap<>();
        for (EventCategoryBoostEntity boost : eventCategoryBoostRepository.findAllByOrderByEventTemplateIdAscMultiplierDesc()) {
            result.computeIfAbsent(boost.getEventTemplateId(), ignored -> new ArrayList<>()).
                    add(new BoostRule(
                            boost.getTag(),
                            boost.getMultiplier(),
                            boost.getRationale(),
                            boost.getSourceTitle(),
                            boost.getSourceUrl()
                    ));
        }
        return result;
    }

    private BoostRule defaultBoost() {
        return new BoostRule(
                "все",
                BigDecimal.ONE,
                "Базовый прогноз без подтвержденного сезонного коэффициента.",
                "Базовая модель",
                ""
        );
    }

    @Data
    @AllArgsConstructor
    private static class EventTemplate {
        private static final Set<String> BROAD_TAGS = Set.of(
                "семья",
                "натуральное",
                "здоровье",
                "иммунитет",
                "локальный продукт",
                "редкий продукт",
                "релакс"
        );

        private Long id;
        private LocalDate date;
        private LocalDate endDate;
        private String title;
        private String type;
        private List<String> tags;
        private List<String> channels;
        private Integer roiPercent;
        private List<BoostRule> boosts;

        boolean intersects(LocalDate periodStart, LocalDate periodEnd) {
            return !endDate.isBefore(periodStart) && !date.isAfter(periodEnd);
        }

        boolean matches(AssortmentItemDto item) {
            List<String> itemValues = matchedValues(item);
            for (String tag : tags) {
                if (containsValue(itemValues, tag)) {
                    return true;
                }
            }
            for (BoostRule boost : boosts) {
                if (boost.getMultiplier().compareTo(BigDecimal.ONE) > 0 && containsValue(itemValues, boost.getTag())) {
                    return true;
                }
            }
            return false;
        }

        EventCandidate bestCandidate(List<AssortmentItemDto> assortment, String categoryFilter, Set<Long> usedAssortmentItemIds) {
            EventCandidate best = null;
            for (AssortmentItemDto item : assortment) {
                if (item.getStock() == null || item.getStock() <= 0) {
                    continue;
                }
                if (categoryFilter != null && !categoryFilter.isBlank() && !item.getCategory().equalsIgnoreCase(categoryFilter)) {
                    continue;
                }
                if (!matches(item)) {
                    continue;
                }

                BoostRule boost = bestBoost(item);
                int score = relevanceScore(item, boost);
                if (usedAssortmentItemIds.contains(item.getId())) {
                    score -= 1200;
                }
                EventCandidate candidate = new EventCandidate(item, boost, score);
                if (best == null || candidate.compareTo(best) > 0) {
                    best = candidate;
                }
            }
            return best;
        }

        BoostRule bestBoost(AssortmentItemDto item) {
            List<String> values = matchedValues(item);
            BoostRule best = null;
            int bestScore = Integer.MIN_VALUE;
            for (BoostRule boost : boosts) {
                if ("все".equalsIgnoreCase(boost.getTag()) || containsValue(values, boost.getTag())) {
                    int score = boost.getMultiplier().multiply(BigDecimal.valueOf(1000)).intValue();
                    if (isExactCategoryBoost(item, boost)) {
                        score += 300;
                    }
                    if (best == null || score > bestScore) {
                        best = boost;
                        bestScore = score;
                    }
                }
            }
            if (best != null) {
                return best;
            }
            return new BoostRule("все", BigDecimal.ONE, "Базовый прогноз без подтвержденного сезонного коэффициента.", "Базовая модель", "");
        }

        String offer(AssortmentItemDto item, PromotionPlan promotion) {
            return "Подборка \"" + title + "\" с акцентом на " + item.getName().toLowerCase(Locale.ROOT)
                    + ", скидкой " + promotion.getDiscountPercent() + "% и промокодом " + promotion.getCode();
        }

        String socialPost(AssortmentItemDto item, PromotionPlan promotion) {
            String product = item.getName().toLowerCase(Locale.ROOT);
            String category = item.getCategory().toLowerCase(Locale.ROOT);
            int variant = Math.floorMod((title + item.getName()).hashCode(), 5);
            String promo = " Промокод " + promotion.getCode() + " дает скидку "
                    + promotion.getDiscountPercent() + "%.";
            String post;

            if (channels.contains("Рецепты") && variant == 0) {
                post = "Рецепт: показать, как использовать " + product + " в блюде под событие \"" + title
                        + "\"; в конце добавить ссылку на товар и набор к нему.";
            } else if (channels.contains("Сторисы") && variant == 1) {
                post = "Сторис-серия: 1) товар крупным планом, 2) короткий факт о фермере, 3) сценарий \"когда пригодится\", 4) кнопка на покупку.";
            } else if ("Праздник".equals(type)) {
                post = "Праздничный пост: собрать идею стола или подарка вокруг товара \"" + item.getName()
                        + "\" и объяснить, почему он уместен именно к событию \"" + title + "\".";
            } else if ("Сезонность".equals(type)) {
                post = "Сезонный пост: показать привычную ситуацию покупателя для категории \"" + category
                        + "\" и предложить " + product + " как готовое решение на ближайшие дни.";
            } else if (variant == 2) {
                post = "Пост-история: коротко рассказать происхождение товара, чем он отличается от массового аналога, и привязать эту ценность к событию.";
            } else {
                post = "Короткий продающий пост: боль покупателя, один аргумент в пользу товара \"" + item.getName()
                        + "\" и призыв сохранить подборку.";
            }
            return post + promo;
        }

        String marketplaceAction(AssortmentItemDto item, PromotionPlan promotion) {
            int variant = Math.floorMod((title + item.getCategory() + item.getName()).hashCode(), 5);
            String promo = "промокод " + promotion.getCode() + " со скидкой " + promotion.getDiscountPercent() + "%";
            if ("Праздник".equals(type)) {
                return switch (variant) {
                    case 0 -> "Собрать праздничный набор: главный SKU, дополняющий товар, короткое описание сценария подарка и " + promo + ".";
                    case 1 -> "Поставить товар в верх тематической витрины, добавить бейдж события и " + promo + " на 72 часа.";
                    default -> "Обновить первое фото под праздничный сценарий, добавить срок предзаказа и " + promo + ".";
                };
            }
            if ("Сезонность".equals(type)) {
                return switch (variant) {
                    case 0 -> "Добавить товар в сезонную подборку, вынести пользу/состав в первые строки описания, закрепить рецепт и " + promo + ".";
                    case 1 -> "Запустить комплект из 2-3 совместимых SKU, показать экономию при покупке набора и " + promo + ".";
                    default -> "Обновить карточку под сезонный запрос: фото использования, ключевой ингредиент и " + promo + ".";
                };
            }
            return switch (variant) {
                case 0 -> "Проверить остатки, поднять товар в подборке тренда и добавить " + promo + " с дедлайном.";
                case 1 -> "Сделать A/B-акцент в названии: событие + главный ингредиент, включить товар в сторис-витрину и добавить " + promo + ".";
                default -> "Добавить к SKU " + promo + ", закрепить отзыв и вывести товар в первый экран категории.";
            };
        }

        String rationale(AssortmentItemDto item, BoostRule boost) {
            return "Теги товара " + item.getTags() + " совпадают с событием; коэффициент роста "
                    + boost.getMultiplier() + " применен по признаку \"" + boost.getTag() + "\". "
                    + boost.getRationale();
        }

        private List<String> matchedValues(AssortmentItemDto item) {
            List<String> values = new ArrayList<>();
            values.add(item.getCategory().toLowerCase(Locale.ROOT));
            values.add(item.getName().toLowerCase(Locale.ROOT));
            for (String tag : item.getTags()) {
                values.add(tag.toLowerCase(Locale.ROOT));
            }
            return values;
        }

        private boolean containsValue(List<String> values, String tag) {
            String normalizedTag = tag.toLowerCase(Locale.ROOT);
            for (String value : values) {
                if (value.equals(normalizedTag) || value.contains(normalizedTag) || aliases(normalizedTag).contains(value)) {
                    return true;
                }
                for (String alias : aliases(normalizedTag)) {
                    if (value.contains(alias)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private int relevanceScore(AssortmentItemDto item, BoostRule boost) {
            List<String> values = matchedValues(item);
            int score = item.getMonthlySales();
            score += Math.min(item.getStock() == null ? 0 : item.getStock(), 120);
            if (item.getStock() != null && item.getStock() < Math.max(8, item.getMonthlySales() * 0.25)) {
                score -= 300;
            }
            for (String tag : tags) {
                if (containsValue(values, tag)) {
                    score += BROAD_TAGS.contains(tag.toLowerCase(Locale.ROOT)) ? 35 : 120;
                }
            }
            if (boost.getMultiplier().compareTo(BigDecimal.ONE) > 0) {
                score += 450 + boost.getMultiplier().multiply(BigDecimal.valueOf(100)).intValue();
                if (isExactCategoryBoost(item, boost)) {
                    score += 520;
                }
            }
            score += titleKeywordScore(item);
            score += categoryPriorityScore(item);
            return score;
        }

        private int titleKeywordScore(AssortmentItemDto item) {
            String titleValue = title.toLowerCase(Locale.ROOT);
            String product = item.getName().toLowerCase(Locale.ROOT);
            String category = item.getCategory().toLowerCase(Locale.ROOT);
            int score = 0;

            if (titleValue.contains("варень") || titleValue.contains("ягод")) {
                if (category.contains("сладости")) {
                    score += 360;
                }
                if (product.contains("варень") || product.contains("джем") || product.contains("пастил")
                        || product.contains("смокв") || product.contains("ягод") || product.contains("мали")
                        || product.contains("клубник") || product.contains("смородин") || product.contains("черник")) {
                    score += 520;
                }
                if (product.contains("песто") || product.contains("соус")) {
                    score -= 700;
                }
            }
            if (titleValue.contains("завтрак")) {
                if (category.contains("молочные") || product.contains("йогурт") || product.contains("творог")
                        || product.contains("молоко") || product.contains("яйц") || product.contains("смузи")) {
                    score += 420;
                }
                if (product.contains("завтрак") || product.contains("каша") || product.contains("мюсли")
                        || product.contains("гранол")) {
                    score += 520;
                }
                if (product.contains("чай")) {
                    score -= 160;
                }
            }
            if (titleValue.contains("пасх")) {
                if (product.contains("пасх") || product.contains("кулич")) {
                    score += 780;
                }
                if (product.contains("творог") || product.contains("творож") || product.contains("яйц")) {
                    score += 460;
                }
                if (category.contains("хлеб") || category.contains("молочные")) {
                    score += 260;
                }
                if (product.contains("пармезан") || product.contains("гауда") || product.contains("качотт")
                        || product.contains("брынз") || product.contains("сулугуни") || product.contains("чечил")) {
                    score -= 460;
                }
            }
            if (titleValue.contains("летняя корзина") || titleValue.contains("ягоды") || titleValue.contains("овощи")) {
                if (category.contains("овощи") || category.contains("фрукты")) {
                    score += 480;
                }
                if (product.contains("ягод") || product.contains("овощ") || product.contains("фрукт")
                        || product.contains("смородин") || product.contains("клубник") || product.contains("малин")
                        || product.contains("черник") || product.contains("яблок") || product.contains("абрикос")) {
                    score += 320;
                }
            }
            if (titleValue.contains("влюблен")) {
                if (category.contains("сладости") || category.contains("мед")) {
                    score += 420;
                }
                if (category.contains("мясо") || product.contains("колбас")) {
                    score -= 360;
                }
            }
            if (titleValue.contains("день россии") || titleValue.contains("народного единства")
                    || titleValue.contains("локальные вкусы")) {
                if (category.contains("сыры") || category.contains("овощи") || category.contains("фрукты")
                        || category.contains("хлеб") || category.contains("мед")) {
                    score += 260;
                }
                if (product.contains("жмых")) {
                    score -= 420;
                }
            }
            if (titleValue.contains("мед")) {
                if (product.contains("мед") || product.contains("мёд") || category.contains("мед")) {
                    score += 520;
                }
            }
            if (titleValue.contains("сыр")) {
                if (category.contains("сыр") || product.contains("сыр")) {
                    score += 420;
                }
            }
            if (titleValue.contains("подар")) {
                if (containsValue(matchedValues(item), "подарок")) {
                    score += 260;
                }
            }
            return score;
        }

        private int categoryPriorityScore(AssortmentItemDto item) {
            String category = item.getCategory().toLowerCase(Locale.ROOT);
            for (BoostRule boost : boosts) {
                if (isExactCategoryBoost(item, boost)) {
                    return 420;
                }
            }
            if (boosts.stream().anyMatch(boost -> "десерт".equalsIgnoreCase(boost.getTag())) && category.contains("сладости")) {
                return 280;
            }
            if (boosts.stream().anyMatch(boost -> "белок".equalsIgnoreCase(boost.getTag()))
                    && (category.contains("мясо") || category.contains("рыба") || category.contains("сыр"))) {
                return 240;
            }
            return 0;
        }

        private boolean isExactCategoryBoost(AssortmentItemDto item, BoostRule boost) {
            return item.getCategory().equalsIgnoreCase(boost.getTag());
        }

        private List<String> aliases(String tag) {
            return switch (tag) {
                case "десерт" -> List.of("сладости", "пастила", "варенье", "джем", "шоколад", "зефир", "конфет");
                case "белок" -> List.of("мясо", "птица", "рыба", "сыр", "яйца", "молочные");
                case "выпечка" -> List.of("хлеб", "булоч", "пирог", "кулич", "блин", "вареники");
                case "натуральное" -> List.of("органич", "фермерск", "мед", "мёд", "ягод", "овощ", "фрукт");
                case "гастрономия" -> List.of("сыр", "мясо", "птица", "рыба", "колбас", "соус", "песто");
                case "подарок" -> List.of("подар", "набор", "премиум", "шоколад", "мед", "мёд", "пастила");
                case "здоровье", "иммунитет" -> List.of("чай", "мед", "мёд", "трав", "ягод", "без сахара");
                case "пасха" -> List.of("кулич", "пасха", "творог", "яйца");
                case "локальный продукт" -> List.of("фермерск", "локальн", "регион");
                case "премиум" -> List.of("премиум", "деликатес", "икра", "морепродукт", "шоколад");
                default -> List.of();
            };
        }
    }

    @Data
    @AllArgsConstructor
    private static class BoostRule {
        private String tag;
        private BigDecimal multiplier;
        private String rationale;
        private String sourceTitle;
        private String sourceUrl;
    }

    @Data
    @AllArgsConstructor
    private static class EventCandidate implements Comparable<EventCandidate> {
        private AssortmentItemDto item;
        private BoostRule boost;
        private Integer score;

        @Override
        public int compareTo(EventCandidate other) {
            int scoreCompare = score.compareTo(other.getScore());
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            return item.getMonthlySales().compareTo(other.getItem().getMonthlySales());
        }
    }

    @Data
    @AllArgsConstructor
    private static class ForecastEconomics implements Comparable<ForecastEconomics> {
        private Integer baselineOrders;
        private Integer seasonalOrders;
        private Integer expectedOrders;
        private Integer upliftPercent;
        private Integer roiPercent;
        private Integer marginPercent;
        private boolean stockLimited;
        private BigDecimal grossRevenue;
        private BigDecimal incrementalRevenue;
        private BigDecimal grossProfit;
        private BigDecimal marketingCost;
        private BigDecimal discountCost;
        private BigDecimal channelCost;
        private BigDecimal operationalCost;

        @Override
        public int compareTo(ForecastEconomics other) {
            int roiCompare = roiPercent.compareTo(other.getRoiPercent());
            if (roiCompare != 0) {
                return roiCompare;
            }
            return expectedOrders.compareTo(other.getExpectedOrders());
        }
    }

    @Data
    @AllArgsConstructor
    private static class PromotionPlan {
        private Integer discountPercent;
        private String code;
    }
}
