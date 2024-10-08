package com.example.spring.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperationDto {

    private Long id;

    private Long fromAccount;

    private Long toAccount;

    private Long sum;

    private String comment;

}
