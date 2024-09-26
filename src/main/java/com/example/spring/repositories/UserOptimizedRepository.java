package com.example.spring.repositories;

import com.example.spring.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOptimizedRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u FROM UserEntity u JOIN FETCH u.accountEntityList a  JOIN FETCH a.accountTypeEntity WHERE u.id = :userId")
    UserEntity findUserWithAccountsAndAccountTypes(Long userId);

}