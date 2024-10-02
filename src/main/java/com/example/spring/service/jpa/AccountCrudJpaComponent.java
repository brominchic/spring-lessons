package com.example.spring.service.jpa;

import com.example.spring.model.dto.AccountDto;
import com.example.spring.model.entity.AccountEntity;
import com.example.spring.repositories.AccountRepository;
import com.example.spring.repositories.UserRepository;
import com.example.spring.service.component.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountCrudJpaComponent implements CrudJpaComponent<AccountDto> {

    private final AccountMapper mapper;
    private final AccountRepository repository;
    private final UserRepository userRepository;

    @Override
    public List<AccountDto> getAll() {
        ArrayList<AccountDto> dtoList = new ArrayList<>();
        ArrayList<AccountEntity> entityList = new ArrayList<>();
        repository.findAll().forEach(entityList::add);
        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.entityToDto(entityList.get(i)));
        }
        return dtoList;
    }

    @Override
    @Transactional
    public AccountDto create(AccountDto dto) throws IOException {
        var newAccount = mapper.dtoToEntity(dto);
        if (dto.getUserId() != null) {
            var user = userRepository.findById(dto.getUserId()).orElseThrow();
            if (user.getAccountEntityList() == null) {
                user.setAccountEntityList(new ArrayList<>());
            }
            user.getAccountEntityList().add(newAccount);
            newAccount.setUserEntity(user);
            user = userRepository.save(user);
            System.out.println();
        }
        return mapper.entityToDto(repository.save(mapper.dtoToEntity(dto)));
    }

    @Override
    @Transactional
    public List<AccountDto> createBatch(List<AccountDto> dList) {
        List<AccountEntity> entityList = new ArrayList<>();
        for (AccountDto dto : dList) {
            entityList.add(mapper.dtoToEntity(dto));
        }
        List<AccountDto> result = new ArrayList<>();
        for (AccountEntity entity : repository.saveAll(entityList)) {
            result.add(mapper.entityToDto(entity));
        }
        return result;
    }
}
