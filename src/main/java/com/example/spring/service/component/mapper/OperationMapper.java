package com.example.spring.service.component.mapper;

import com.example.spring.config.ApplicationConfig;
import com.example.spring.model.dto.OperationDto;
import com.example.spring.model.entity.OperationEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
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
        return OperationEntity.builder().
                id(operationDto.getId()).
                sum(operationDto.getSum()).
                comment(operationDto.getComment()).
                fromAccount(operationDto.getFromAccount()).
                toAccount(operationDto.getToAccount()).build();
    }

    @Override
    public OperationDto stringToDto(String string) throws JsonProcessingException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        ObjectMapper objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
        return objectMapper.readValue(string, OperationDto.class);
    }

}
