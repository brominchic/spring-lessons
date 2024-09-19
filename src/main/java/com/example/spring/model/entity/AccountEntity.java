package com.example.spring.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "accounts")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountEntity {

    @Id
    private Long number;

    @Column(name = "balance")
    private Long balance;

    @Column(name = "type")
    private Long type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

}