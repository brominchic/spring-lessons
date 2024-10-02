package com.example.spring.repositories;

import com.example.spring.model.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<UserEntity, Long> {

    @Query("SELECT u FROM UserEntity u JOIN FETCH u.accountEntityList a  JOIN FETCH a.accountTypeEntity WHERE u.id = :userId")
    UserEntity findUserWithAccountsAndAccountTypes(Long userId);

    @EntityGraph(attributePaths = "accountEntityList")
        // с помощью этой аннотации JPA загружает все в одном запросе SELECT
    UserEntity findUserById(Long id);
}
