package com.example.spring.model.mapper;

import com.example.spring.model.dto.OperationDto;
import com.example.spring.model.entity.OperationEntity;
import org.springframework.stereotype.Component;

@Component
public class OperationMapper implements Mapper<OperationDto, OperationEntity> {
    public OperationDto entityToDto(OperationEntity operationEntity) {
        return OperationDto.builder().
                id(operationEntity.getId()).
                sum(operationEntity.getSum()).
                comment(operationEntity.getComment()).
                fromAccount(operationEntity.getFromAccount()).
                toAccount(operationEntity.getToAccount()).build();
    }

    public OperationEntity dtoToEntity(OperationDto operationDto) {
        return new OperationEntity(operationDto.getId(), operationDto.getFromAccount(), operationDto.getToAccount(), operationDto.getSum(), operationDto.getComment());
    }
}
