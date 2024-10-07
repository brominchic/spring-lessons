package com.example.spring.repositories;

import com.example.spring.model.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface UserRepository extends CrudRepository<UserEntity, Long> {

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.accountEntityList a  LEFT JOIN FETCH a.accountTypeEntity WHERE u.id = :userId")
    Optional<UserEntity> findUserWithAccountsAndAccountTypesById(Long userId);

    @EntityGraph(attributePaths = "accountEntityList")
        // с помощью этой аннотации JPA загружает все в одном запросе SELECT
    Optional<UserEntity> findUserById(Long id);
}
