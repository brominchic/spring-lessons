package com.example.spring.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserWithAccountsDto {
    private Long id;

    private String fullName;

    private Long totalBalance;

    private List<AccountDto> accounts;
}
