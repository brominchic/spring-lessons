package com.example.spring.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "accounts")
@Entity
@Data
public class AccountEntity {

    @Id
    private Long number;

    @Column(name = "balance")
    private Long balance;

    @Column(name = "type")
    private Long type;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}