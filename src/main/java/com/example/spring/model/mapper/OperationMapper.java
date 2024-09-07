package com.example.spring.model.mapper;

import com.example.spring.config.ApplicationConfig;
import com.example.spring.model.dto.OperationDto;
import com.example.spring.model.entity.OperationEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<OperationDto> listStringToListDto(String string) throws JsonProcessingException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        ObjectMapper objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
        List<OperationDto> dtoList = objectMapper.readValue(string, new TypeReference<>() {
        });
        return dtoList;
    }

    @Override
    public List<OperationEntity> listStringToListEntity(String string) throws JsonProcessingException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();
        ObjectMapper objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
        List<OperationDto> dtoList = objectMapper.readValue(string, new TypeReference<>() {
        });
        List<OperationEntity> entityList = new ArrayList<>();
        for (int i = 0; i < dtoList.size(); i++) {
            entityList.add(this.dtoToEntity(dtoList.get(i)));
        }
        return entityList;
    }
}
