package com.example.spring.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @Min(value = 0L)
    private Long id;

    @NotNull
    @Length(min = 2, max = 10)
    private String fullName;

    @NotNull
    @Min(value = 0L)
    private Long totalBalance;

    private List<Long> accounts;

}
