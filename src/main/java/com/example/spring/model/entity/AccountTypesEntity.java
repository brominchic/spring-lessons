package com.example.spring.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "accounts")
@Entity
@Data
public class AccountTypesEntity {

    @Id
    private Long id;

    @Column(name = "name")
    private Long name;

}
