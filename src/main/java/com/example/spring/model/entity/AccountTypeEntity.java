package com.example.spring.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "account_types")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountTypeEntity {

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

}
