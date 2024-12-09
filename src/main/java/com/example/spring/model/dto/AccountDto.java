package com.example.spring.model.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDto {
    @NotNull
    @Min(value = 0L)
    private Long number;

    @NotNull
    @Min(value = 0L)
    private Long balance;

    @NotNull
    @Min(value = 0L)
    private Long type;

    @NotNull
    @Min(value = 0L)
    private Long userId;

}
