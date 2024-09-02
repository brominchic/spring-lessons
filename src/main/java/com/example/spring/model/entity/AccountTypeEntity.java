package com.example.spring.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "account_types")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountTypeEntity {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(name = "name")
    private Long name;

}
