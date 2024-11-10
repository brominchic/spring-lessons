package com.example.spring.service.component.mapper;

import com.example.spring.model.dto.OperationDto;
import com.example.spring.model.entity.AccountEntity;
import com.example.spring.model.entity.OperationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OperationMapper implements Mapper<OperationDto, OperationEntity> {

    @Override
    public OperationDto entityToDto(OperationEntity operationEntity) {
        return OperationDto.builder().
                id(operationEntity.getId()).
                sum(operationEntity.getSum()).
                comment(operationEntity.getComment()).
                fromAccount(operationEntity.getFromAccount().getNumber()).
                toAccount(operationEntity.getToAccount().getNumber()).build();
    }

    @Override
    public OperationEntity dtoToEntity(OperationDto operationDto) {
        return OperationEntity.builder().
                id(operationDto.getId()).
                sum(operationDto.getSum()).
                comment(operationDto.getComment()).
                fromAccount(AccountEntity
                        .builder()
                        .number(operationDto.getFromAccount())
                        .build()).
                toAccount(AccountEntity
                        .builder()
                        .number(operationDto.getToAccount())
                        .build())
                .build();
    }

}
