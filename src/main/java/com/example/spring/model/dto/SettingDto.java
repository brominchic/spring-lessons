package com.example.spring.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SettingDto {
    @NotNull
    @Min(value = 0L)
    private Long id;

    @NotNull
    @Length(min = 2, max = 10)
    private String name;

}
