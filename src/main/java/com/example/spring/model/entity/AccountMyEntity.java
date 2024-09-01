package com.example.spring.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "accounts")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountMyEntity {

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