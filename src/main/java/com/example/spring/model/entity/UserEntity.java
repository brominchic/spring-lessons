package com.example.spring.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "users")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    private Long id;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "total_balance")
    private Long totalBalance;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "number", cascade = CascadeType.ALL)
    private List<AccountEntity> accountEntityList = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "users_settings",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "setting_id", referencedColumnName = "id"))
    private Set<SettingEntity> settings = new HashSet<SettingEntity>();
}
