package com.example.spring.repositories;

import com.example.spring.model.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<UserEntity, Long> {

    @Query("SELECT u FROM UserEntity u JOIN FETCH u.accountEntityList a  JOIN FETCH a.accountTypeEntity WHERE u.id = :userId")
    UserEntity findUserWithAccountsAndAccountTypes(Long userId);
}
