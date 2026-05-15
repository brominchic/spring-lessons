package com.example.spring.service.component.mapper;

import com.example.spring.model.dto.marketing.AssortmentItemDto;
import com.example.spring.model.entity.marketing.AssortmentItemEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Locale;

@Component
public class AssortmentItemMapper implements Mapper<AssortmentItemDto, AssortmentItemEntity> {

    @Override
    public AssortmentItemDto entityToDto(AssortmentItemEntity item) {
        return AssortmentItemDto.builder().
                id(item.getId()).
                organizationId(item.getFarmer().getId()).
                productId(item.getProductId()).
                shopName(item.getFarmer().getShopName()).
                farmerDescription(item.getFarmer().getDescription()).
                region(item.getFarmer().getRegion()).
                name(item.getName()).
                category(normalizedCategory(item)).
                productDescription(item.getProductDescription()).
                urlProduct(item.getUrlProduct()).
                urlFarmer(item.getFarmer().getUrlFarmer()).
                price(item.getPrice()).
                stock(item.getStock()).
                monthlySales(item.getMonthlySales()).
                tags(new ArrayList<>(item.getTags())).
                build();
    }

    private String normalizedCategory(AssortmentItemEntity item) {
        String category = item.getCategory();
        String value = ((category == null ? "" : category) + " "
                + (item.getName() == null ? "" : item.getName()) + " "
                + (item.getProductDescription() == null ? "" : item.getProductDescription()) + " "
                + (item.getUrlProduct() == null ? "" : item.getUrlProduct())).toLowerCase(Locale.ROOT);
        String tokens = " " + value.replaceAll("[^a-zа-яё0-9]+", " ") + " ";

        if (containsAny(tokens, " говяд", " свинин", " баранин", " курин", " индей", " мясн", " мясо ",
                " стейк", " ростбиф", " тушен", " колбас", " mjas", " myas")) {
            return "Мясо и птица";
        }
        if (containsAny(tokens, " рыб", " морепродукт", " икра ", " seafood ", " fish ")) {
            return "Рыба и морепродукты";
        }
        if (containsAny(tokens, " сыр ", " сыры ", " сырн", " брынз", " рикотт", " cheese ")) {
            return "Сыры";
        }
        if (containsAny(tokens, " мед ", " мёд ", " honey ")) {
            return "Мед и пчеловодство";
        }
        if (containsAny(tokens, " хлеб", " булоч", " выпеч", " лаваш")) {
            return "Хлеб и выпечка";
        }
        return category;
    }

    private boolean containsAny(String value, String... needles) {
        for (String needle : needles) {
            if (value.contains(needle)) {
                return true;
            }
        }
        return false;
    }
}
